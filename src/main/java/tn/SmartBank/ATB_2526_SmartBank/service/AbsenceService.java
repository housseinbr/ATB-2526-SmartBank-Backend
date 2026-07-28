package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.entity.Abcence;
import tn.SmartBank.ATB_2526_SmartBank.repository.AbcenceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AbsenceService {

    private final AbcenceRepository abcenceRepository;

    // CREATE
    public Abcence create(Abcence abcence) {
        validateDates(abcence);

        // Par défaut, une nouvelle demande est toujours EN_ATTENTE
        if (abcence.getStatus() == null) {
            abcence.setStatus(Status.EN_ATTENTE);
        }

        return abcenceRepository.save(abcence);
    }

    // GET BY ID
    public Abcence getById(Long id) {
        return abcenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Absence non trouvée avec l'id : " + id));
    }

    // GET ALL (pour admin/superviseur)
    public List<Abcence> getAll() {
        return abcenceRepository.findAll();
    }

    // GET BY USER ID (pour un employé qui ne voit que ses demandes)
    public List<Abcence> findByUserId(Long userId) {
        return abcenceRepository.findByUser_Id(userId);
    }

    // UPDATE
    public Abcence update(Long id, Abcence abcence, Long currentUserId) {
        Abcence existing = getById(id);

        // Sécurité : est-ce bien SA demande ?
        if (!existing.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Vous ne pouvez modifier que vos propres demandes");
        }

        // On ne modifie que si elle est encore en attente
        if (existing.getStatus() != Status.EN_ATTENTE) {
            throw new IllegalStateException("Seules les demandes en attente peuvent être modifiées");
        }

        validateDates(abcence);

        existing.setType(abcence.getType());
        existing.setComment(abcence.getComment());
        existing.setDateStart(abcence.getDateStart());
        existing.setDateEnd(abcence.getDateEnd());
        existing.setDemiJournee(abcence.getDemiJournee());
        // Le statut reste EN_ATTENTE, le user ne change pas

        return abcenceRepository.save(existing);
    }

    // DELETE
    public void delete(Long id, Long currentUserId) {
        Abcence existing = getById(id);

        // Sécurité : est-ce bien SA demande ?
        if (!existing.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Vous ne pouvez supprimer que vos propres demandes");
        }

        // On ne supprime que si elle est encore en attente
        if (existing.getStatus() != Status.EN_ATTENTE) {
            throw new IllegalStateException("Seules les demandes en attente peuvent être supprimées");
        }

        abcenceRepository.delete(existing);
    }

    // VALIDATION
    private void validateDates(Abcence abcence) {
        if (abcence.getDateStart() == null || abcence.getDateEnd() == null) {
            throw new IllegalArgumentException("La date de début et la date de fin sont obligatoires");
        }
        if (abcence.getDateEnd().isBefore(abcence.getDateStart())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être avant la date de début");
        }
    }
}