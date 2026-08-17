package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande_Mobilite;
import tn.SmartBank.ATB_2526_SmartBank.entity.Mobilite;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.repository.Demande_MobiliteRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DemandeMobiliteService {

    private final Demande_MobiliteRepository demandeMobiliteRepository;
    private final MobiliteService mobiliteService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<Demande_Mobilite> getMyRequests(Long userId) {
        return demandeMobiliteRepository.findByUser_IdOrderByDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Demande_Mobilite> getManagedRequests(Long actorId) {
        User actor = userService.getUserById(actorId);
        if (actor.getRole() == Role.ADMIN) {
            return demandeMobiliteRepository.findAll().stream()
                    .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                    .toList();
        }
        if (actor.getRole() == Role.SUPERVISEUR) {
            return demandeMobiliteRepository.findByUser_Superviseur_IdOrderByDateDesc(actorId);
        }
        return demandeMobiliteRepository.findByUser_IdOrderByDateDesc(actorId);
    }

    @Transactional(readOnly = true)
    public List<Demande_Mobilite> getManagedPendingRequests(Long actorId) {
        User actor = userService.getUserById(actorId);
        if (actor.getRole() == Role.ADMIN) {
            return demandeMobiliteRepository.findAll().stream()
                    .filter(request -> request.getStatus() == Status.EN_ATTENTE)
                    .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                    .toList();
        }
        if (actor.getRole() == Role.SUPERVISEUR) {
            return demandeMobiliteRepository.findByUser_Superviseur_IdAndStatusOrderByDateDesc(actorId, Status.EN_ATTENTE);
        }
        return demandeMobiliteRepository.findByUser_IdOrderByDateDesc(actorId).stream()
                .filter(request -> request.getStatus() == Status.EN_ATTENTE)
                .toList();
    }

    public Demande_Mobilite create(Long mobiliteId, Long currentUserId) {
        User currentUser = userService.getUserById(currentUserId);
        Mobilite mobilite = mobiliteService.getById(mobiliteId);

        Demande_Mobilite demande = Demande_Mobilite.builder()
                .mobilite(mobilite)
                .user(currentUser)
                .status(Status.EN_ATTENTE)
                .date(LocalDate.now())
                .build();
        Demande_Mobilite saved = demandeMobiliteRepository.save(demande);

        if (currentUser.getSuperviseur() != null) {
            notificationService.create(
                    currentUser.getSuperviseur(),
                    "Nouvelle demande de mobilité",
                    currentUser.getFirstName() + " " + currentUser.getLastName() + " a soumis une demande de mobilité."
            );
        }

        return saved;
    }

    public Demande_Mobilite decide(Long id, Long actorId, Status decision) {
        if (decision != Status.VALIDE && decision != Status.REFUSE) {
            throw new IllegalArgumentException("Décision invalide");
        }
        User actor = userService.getUserById(actorId);
        Demande_Mobilite demande = demandeMobiliteRepository.findByIdDemande(id)
                .orElseThrow(() -> new RuntimeException("Demande de mobilité introuvable"));
        ensureCanManage(actor, demande.getUser());
        if (demande.getStatus() != Status.EN_ATTENTE) {
            throw new IllegalStateException("Cette demande a déjà été traitée");
        }
        demande.setStatus(decision);
        notificationService.create(
                demande.getUser(),
                decision == Status.VALIDE ? "Mobilité validée" : "Mobilité refusée",
                "Votre demande de mobilité a été " + (decision == Status.VALIDE ? "validée" : "refusée") + "."
        );
        return demandeMobiliteRepository.save(demande);
    }

    private void ensureCanManage(User actor, User target) {
        if (actor.getRole() == Role.ADMIN) return;
        if (actor.getRole() != Role.SUPERVISEUR) throw new AccessDeniedException("Accès refusé");
        if (target.getSuperviseur() == null || !target.getSuperviseur().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Vous ne pouvez traiter que les demandes de votre équipe");
        }
    }
}
