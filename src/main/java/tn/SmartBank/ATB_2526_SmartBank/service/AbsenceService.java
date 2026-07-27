package tn.SmartBank.ATB_2526_SmartBank.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

        return abcenceRepository.save(abcence);
    }

    // GET BY ID
    public Abcence getById(Long id) {

        return abcenceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Absence non trouvée avec l'id : " + id
                        )
                );
    }

    // GET ALL
    public List<Abcence> getAll() {

        return abcenceRepository.findAll();
    }

    // UPDATE
    public Abcence update(Long id, Abcence abcence) {

        Abcence existingAbcence = getById(id);

        validateDates(abcence);

        existingAbcence.setUser(abcence.getUser());
        existingAbcence.setType(abcence.getType());
        existingAbcence.setComment(abcence.getComment());
        existingAbcence.setDateStart(abcence.getDateStart());
        existingAbcence.setDateEnd(abcence.getDateEnd());
        existingAbcence.setDemiJournee(abcence.getDemiJournee());
        existingAbcence.setStatus(abcence.getStatus());

        return abcenceRepository.save(existingAbcence);
    }

    // DELETE
    public void delete(Long id) {

        Abcence abcence = getById(id);

        abcenceRepository.delete(abcence);
    }

    // VALIDATION
    private void validateDates(Abcence abcence) {

        if (abcence.getDateStart() == null ||
                abcence.getDateEnd() == null) {

            throw new IllegalArgumentException(
                    "La date de début et la date de fin sont obligatoires"
            );
        }

        if (abcence.getDateEnd()
                .isBefore(abcence.getDateStart())) {

            throw new IllegalArgumentException(
                    "La date de fin ne peut pas être avant la date de début"
            );
        }
    }
}
