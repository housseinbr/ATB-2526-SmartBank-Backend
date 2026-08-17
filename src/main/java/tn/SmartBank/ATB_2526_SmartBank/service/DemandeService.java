package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Type_Demande;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.repository.DemandeRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DemandeService {

    private final DemandeRepository demandeRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<Demande> getMyRequests(Long userId) {
        return demandeRepository.findByUser_IdOrderByDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Demande> getManagedRequests(Long actorId) {
        User actor = userService.getUserById(actorId);
        if (actor.getRole() == Role.ADMIN) {
            return demandeRepository.findAll().stream()
                    .sorted((left, right) -> right.getDate().compareTo(left.getDate()))
                    .toList();
        }
        if (actor.getRole() == Role.SUPERVISEUR) {
            return demandeRepository.findByUser_Superviseur_IdOrderByDateDesc(actorId);
        }
        return demandeRepository.findByUser_IdOrderByDateDesc(actorId);
    }

    @Transactional(readOnly = true)
    public List<Demande> getManagedPendingRequests(Long actorId) {
        User actor = userService.getUserById(actorId);
        if (actor.getRole() == Role.ADMIN) {
            return demandeRepository.findAll().stream()
                    .filter(item -> item.getStatus() == Status.EN_ATTENTE)
                    .sorted((left, right) -> right.getDate().compareTo(left.getDate()))
                    .toList();
        }
        if (actor.getRole() == Role.SUPERVISEUR) {
            return demandeRepository.findByUser_Superviseur_IdAndStatusOrderByDateDesc(actorId, Status.EN_ATTENTE);
        }
        return demandeRepository.findByUser_IdOrderByDateDesc(actorId).stream()
                .filter(item -> item.getStatus() == Status.EN_ATTENTE)
                .toList();
    }

    public Demande create(Long userId, Type_Demande type) {
        User user = userService.getUserById(userId);
        Demande demande = Demande.builder()
                .user(user)
                .type(type)
                .status(Status.EN_ATTENTE)
                .date(LocalDate.now())
                .build();
        Demande saved = demandeRepository.save(demande);
        if (user.getSuperviseur() != null) {
            notificationService.create(
                    user.getSuperviseur(),
                    "Nouvelle demande " + type.name().toLowerCase(),
                    user.getFirstName() + " " + user.getLastName() + " a soumis une demande " + type.name().toLowerCase() + "."
            );
        }
        return saved;
    }

    public Demande decide(Long id, Long actorId, Status decision) {
        if (decision != Status.VALIDE && decision != Status.REFUSE) {
            throw new IllegalArgumentException("Décision invalide");
        }
        User actor = userService.getUserById(actorId);
        Demande demande = demandeRepository.findByIdDemande(id)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));
        ensureCanManage(actor, demande.getUser());
        if (demande.getStatus() != Status.EN_ATTENTE) {
            throw new IllegalStateException("Cette demande a déjà été traitée");
        }
        demande.setStatus(decision);
        notificationService.create(
                demande.getUser(),
                decision == Status.VALIDE ? "Demande validée" : "Demande refusée",
                "Votre demande " + demande.getType().name().toLowerCase() + " a été " + (decision == Status.VALIDE ? "validée" : "refusée") + "."
        );
        return demandeRepository.save(demande);
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
