package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande_Formation;
import tn.SmartBank.ATB_2526_SmartBank.entity.Formation;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.repository.Demande_FormationRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DemandeFormationService {

    private final Demande_FormationRepository demandeFormationRepository;
    private final FormationService formationService;
    private final UserService userService;
    private final CompetanceService competanceService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<Demande_Formation> getMyRequests(Long userId) {
        return demandeFormationRepository.findByUser_IdOrderByDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Demande_Formation> getManagedRequests(Long actorId) {
        User actor = userService.getUserById(actorId);
        if (actor.getRole() == Role.ADMIN) {
            return demandeFormationRepository.findAllByOrderByDateDesc();
        }
        if (actor.getRole() == Role.SUPERVISEUR) {
            return demandeFormationRepository.findByUser_Superviseur_IdOrderByDateDesc(actorId);
        }
        return demandeFormationRepository.findByUser_IdOrderByDateDesc(actorId);
    }

    @Transactional(readOnly = true)
    public List<Demande_Formation> getManagedPendingRequests(Long actorId) {
        User actor = userService.getUserById(actorId);
        if (actor.getRole() == Role.ADMIN) {
            return demandeFormationRepository.findAllByOrderByDateDesc().stream()
                    .filter(request -> request.getStatus() == Status.EN_ATTENTE)
                    .toList();
        }
        if (actor.getRole() == Role.SUPERVISEUR) {
            return demandeFormationRepository.findByUser_Superviseur_IdAndStatusOrderByDateDesc(actorId, Status.EN_ATTENTE);
        }
        return demandeFormationRepository.findByUser_IdOrderByDateDesc(actorId).stream()
                .filter(request -> request.getStatus() == Status.EN_ATTENTE)
                .toList();
    }

    public Demande_Formation create(Long formationId, Long currentUserId) {
        User currentUser = userService.getUserById(currentUserId);
        Formation formation = formationService.getById(formationId);

        if (competanceService.existsForUserAndFormation(currentUserId, formationId)) {
            throw new IllegalStateException("Cette formation est déjà ajoutée comme compétence");
        }

        if (demandeFormationRepository.existsByUser_IdAndFormation_IdFormationAndStatusIn(
                currentUserId,
                formationId,
                List.of(Status.EN_ATTENTE, Status.VALIDE))) {
            throw new IllegalStateException("Une demande pour cette formation existe déjà");
        }

        Demande_Formation demande = Demande_Formation.builder()
                .formation(formation)
                .user(currentUser)
                .status(Status.EN_ATTENTE)
                .date(LocalDate.now())
                .build();
        Demande_Formation saved = demandeFormationRepository.save(demande);

        if (currentUser.getSuperviseur() != null) {
            notificationService.create(
                    currentUser.getSuperviseur(),
                    "Nouvelle demande de formation",
                    currentUser.getFirstName() + " " + currentUser.getLastName() +
                            " a sélectionné la formation " + formation.getTitle() + "."
            );
        }

        return saved;
    }

    public Demande_Formation decide(Long id, Long actorId, Status decision) {
        if (decision != Status.VALIDE && decision != Status.REFUSE) {
            throw new IllegalArgumentException("Décision invalide");
        }

        User actor = userService.getUserById(actorId);
        Demande_Formation demande = demandeFormationRepository.findByIdDemandeFormation(id)
                .orElseThrow(() -> new RuntimeException("Demande de formation introuvable avec l'id : " + id));

        ensureCanManage(actor, demande.getUser());

        if (demande.getStatus() != Status.EN_ATTENTE) {
            throw new IllegalStateException("Cette demande a déjà été traitée");
        }

        if (decision == Status.VALIDE) {
            competanceService.createIfAbsent(demande.getUser(), demande.getFormation());
            demande.setStatus(Status.VALIDE);
            notificationService.create(
                    demande.getUser(),
                    "Formation validée",
                    "Votre demande pour la formation " + demande.getFormation().getTitle() + " a été validée."
            );
        } else {
            demande.setStatus(Status.REFUSE);
            notificationService.create(
                    demande.getUser(),
                    "Formation refusée",
                    "Votre demande pour la formation " + demande.getFormation().getTitle() + " a été refusée."
            );
        }

        return demandeFormationRepository.save(demande);
    }

    private void ensureCanManage(User actor, User target) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        if (actor.getRole() != Role.SUPERVISEUR) {
            throw new AccessDeniedException("Accès refusé");
        }
        if (target.getSuperviseur() == null || !target.getSuperviseur().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Vous ne pouvez traiter que les demandes de votre équipe");
        }
    }
}
