package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Role;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.entity.Abcence;
import tn.SmartBank.ATB_2526_SmartBank.entity.History_Sold;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.repository.AbcenceRepository;
import tn.SmartBank.ATB_2526_SmartBank.repository.History_SoldRepository;
import tn.SmartBank.ATB_2526_SmartBank.repository.UserRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AbsenceService {

    private static final double MONTHLY_ACCRUAL = 1.83d;
    private static final double INITIAL_BALANCE = 22d;

    private final AbcenceRepository abcenceRepository;
    private final History_SoldRepository historySoldRepository;
    private final UserRepository userRepository;

    public Abcence create(Abcence abcence, Long currentUserId) {
        User currentUser = getUser(currentUserId);
        ensureLeaveBalanceInitialized(currentUser);

        validateDates(abcence);
        ensureNoOverlap(currentUser.getId(), abcence.getDateStart(), abcence.getDateEnd(), null);

        abcence.setUser(currentUser);
        abcence.setStatus(Status.EN_ATTENTE);
        return abcenceRepository.save(abcence);
    }

    @Transactional(readOnly = true)
    public Abcence getById(Long id) {
        return abcenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Absence non trouvée avec l'id : " + id));
    }

    @Transactional(readOnly = true)
    public List<Abcence> getAll() {
        return abcenceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Abcence> findByUserId(Long userId) {
        return abcenceRepository.findByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public List<Abcence> findTeamAbsences(Long supervisorId) {
        return abcenceRepository.findByUser_Superviseur_Id(supervisorId);
    }

    @Transactional(readOnly = true)
    public List<Abcence> findTeamPendingAbsences(Long supervisorId) {
        return abcenceRepository.findByUser_Superviseur_IdAndStatus(supervisorId, Status.EN_ATTENTE);
    }

    @Transactional(readOnly = true)
    public List<Abcence> findMyAbsences(Long userId) {
        return abcenceRepository.findByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public List<History_Sold> findHistoryForUser(Long userId) {
        return historySoldRepository.findByUser_IdOrderByDateActionDesc(userId);
    }

    public Abcence update(Long id, Abcence abcence, Long currentUserId) {
        Abcence existing = getById(id);
        if (!existing.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Vous ne pouvez modifier que vos propres demandes");
        }
        if (existing.getStatus() != Status.EN_ATTENTE) {
            throw new IllegalStateException("Seules les demandes en attente peuvent être modifiées");
        }

        validateDates(abcence);
        ensureNoOverlap(existing.getUser().getId(), abcence.getDateStart(), abcence.getDateEnd(), existing.getIdAbcance());

        existing.setType(abcence.getType());
        existing.setComment(abcence.getComment());
        existing.setDateStart(abcence.getDateStart());
        existing.setDateEnd(abcence.getDateEnd());
        existing.setDemiJournee(abcence.getDemiJournee());
        return abcenceRepository.save(existing);
    }

    public void delete(Long id, Long currentUserId) {
        Abcence existing = getById(id);
        if (!existing.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Vous ne pouvez supprimer que vos propres demandes");
        }
        if (existing.getStatus() != Status.EN_ATTENTE) {
            throw new IllegalStateException("Seules les demandes en attente peuvent être supprimées");
        }

        abcenceRepository.delete(existing);
    }

    public Abcence decide(Long id, Long actorId, Status decision) {
        if (decision != Status.VALIDE && decision != Status.REFUSE) {
            throw new IllegalArgumentException("Décision invalide");
        }

        User actor = getUser(actorId);
        Abcence absence = getById(id);
        User target = absence.getUser();

        ensureCanManage(actor, target);

        if (absence.getStatus() != Status.EN_ATTENTE) {
            throw new IllegalStateException("Cette demande a déjà été traitée");
        }

        if (decision == Status.VALIDE) {
            double duration = calculateDays(absence);
            User managedUser = ensureLeaveBalanceInitialized(target);
            double balanceBefore = managedUser.getSolde();
            if (balanceBefore < duration) {
                throw new IllegalStateException("Le solde est insuffisant pour valider cette absence");
            }

            managedUser.setSolde(roundBalance(balanceBefore - duration));
            userRepository.save(managedUser);
            recordBalanceChange(managedUser, absence, "ABSENCE_VALIDEE", balanceBefore, managedUser.getSolde());
            absence.setStatus(Status.VALIDE);
        } else {
            absence.setStatus(Status.REFUSE);
        }

        return abcenceRepository.save(absence);
    }

    public void addMonthlyAccruals() {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.EMPLOYE || user.getRole() == Role.SUPERVISEUR)
                .toList();

        for (User user : users) {
            User managedUser = ensureLeaveBalanceInitialized(user);
            double before = managedUser.getSolde();
            double after = roundBalance(before + MONTHLY_ACCRUAL);
            managedUser.setSolde(after);
            userRepository.save(managedUser);
            recordBalanceChange(managedUser, null, "ACQUISITION_MENSUELLE", before, after);
        }
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void scheduledMonthlyAccruals() {
        addMonthlyAccruals();
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

    private void ensureNoOverlap(Long userId, LocalDate dateStart, LocalDate dateEnd, Long excludeId) {
        List<Abcence> overlaps = excludeId == null
                ? abcenceRepository.findOverlapping(userId, dateStart, dateEnd, Status.REFUSE)
                : abcenceRepository.findOverlappingExcludingSelf(userId, dateStart, dateEnd, excludeId, Status.REFUSE);

        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Une demande d'absence existe déjà sur cette période");
        }
    }

    private void validateDates(Abcence abcence) {
        if (abcence.getDateStart() == null || abcence.getDateEnd() == null) {
            throw new IllegalArgumentException("La date de début et la date de fin sont obligatoires");
        }
        if (abcence.getDateEnd().isBefore(abcence.getDateStart())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être avant la date de début");
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'id : " + userId));
    }

    private User ensureLeaveBalanceInitialized(User user) {
        if (user.getSolde() == null) {
            user.setSolde(INITIAL_BALANCE);
            return userRepository.save(user);
        }
        return user;
    }

    private double calculateDays(Abcence absence) {
        long inclusiveDays = ChronoUnit.DAYS.between(absence.getDateStart(), absence.getDateEnd()) + 1;
        if (inclusiveDays == 1 && absence.getDemiJournee() != null) {
            return 0.5d;
        }
        return inclusiveDays;
    }

    private void recordBalanceChange(User user, Abcence absence, String motif, double before, double after) {
        History_Sold history = History_Sold.builder()
                .user(user)
                .abcence(absence)
                .motif(motif)
                .dateAction(LocalDate.now())
                .soldeBefore(roundBalance(before))
                .soldeAfter(roundBalance(after))
                .build();
        historySoldRepository.save(history);
    }

    private double roundBalance(double value) {
        return Math.round(value * 100.0d) / 100.0d;
    }
}
