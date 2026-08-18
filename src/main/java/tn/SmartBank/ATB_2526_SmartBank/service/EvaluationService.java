package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.entity.Evaluation;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.repository.EvaluationRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<Evaluation> getMyEvaluations(Long userId) {
        return evaluationRepository.findByUser_IdOrderByDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Evaluation> getManagedEvaluations(Long actorId) {
        User actor = userService.getUserById(actorId);
        if (actor.getRole() == Role.ADMIN) {
            return evaluationRepository.findAll().stream().sorted((a, b) -> b.getDate().compareTo(a.getDate())).toList();
        }
        if (actor.getRole() == Role.SUPERVISEUR) {
            return evaluationRepository.findBySuperviseur_IdOrderByDateDesc(actorId);
        }
        return evaluationRepository.findByUser_IdOrderByDateDesc(actorId);
    }

    @Transactional(readOnly = true)
    public List<Evaluation> getManagedPendingEvaluations(Long actorId) {
        User actor = userService.getUserById(actorId);
        if (actor.getRole() == Role.ADMIN) {
            return evaluationRepository.findAll().stream()
                    .filter(evaluation -> evaluation.getStatus() == Status.EN_ATTENTE)
                    .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                    .toList();
        }
        if (actor.getRole() == Role.SUPERVISEUR) {
            return evaluationRepository.findBySuperviseur_IdAndStatusOrderByDateDesc(actorId, Status.EN_ATTENTE);
        }
        return evaluationRepository.findByUser_IdOrderByDateDesc(actorId).stream()
                .filter(evaluation -> evaluation.getStatus() == Status.EN_ATTENTE)
                .toList();
    }

    public Evaluation create(Long userId, Long supervisorId, Evaluation evaluation) {
        User target = userService.getUserById(userId);
        User supervisor = userService.getUserById(supervisorId);
        if (supervisor.getRole() == Role.EMPLOYE) {
            throw new IllegalArgumentException("Un employé ne peut pas créer une évaluation");
        }
        if (supervisor.getRole() == Role.SUPERVISEUR && (target.getSuperviseur() == null
                || !target.getSuperviseur().getId().equals(supervisorId))) {
            throw new AccessDeniedException("Vous ne pouvez évaluer que les employés de votre équipe");
        }
        evaluation.setUser(target);
        evaluation.setSuperviseur(supervisor);
        evaluation.setStatus(Status.EN_ATTENTE);
        if (evaluation.getDate() == null) {
            evaluation.setDate(LocalDate.now());
        }
        Evaluation saved = evaluationRepository.save(evaluation);
        notificationService.create(target, "Nouvelle évaluation", "Une évaluation a été préparée pour votre profil.");
        return saved;
    }

    public Evaluation decide(Long id, Long actorId, Status decision) {
        if (decision != Status.VALIDE && decision != Status.REFUSE) {
            throw new IllegalArgumentException("Décision invalide");
        }
        User actor = userService.getUserById(actorId);
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Évaluation introuvable"));
        ensureCanManage(actor, evaluation.getUser());
        if (evaluation.getStatus() != Status.EN_ATTENTE) {
            throw new IllegalStateException("Cette évaluation a déjà été traitée");
        }
        evaluation.setStatus(decision);
        notificationService.create(
                evaluation.getUser(),
                decision == Status.VALIDE ? "Évaluation validée" : "Évaluation refusée",
                "Votre évaluation a été " + (decision == Status.VALIDE ? "validée" : "refusée") + "."
        );
        return evaluationRepository.save(evaluation);
    }

    private void ensureCanManage(User actor, User target) {
        if (actor.getRole() == Role.ADMIN) return;
        if (actor.getRole() != Role.SUPERVISEUR) throw new AccessDeniedException("Accès refusé");
        if (target.getSuperviseur() == null || !target.getSuperviseur().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Vous ne pouvez traiter que les demandes de votre équipe");
        }
    }
}
