package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.SmartBank.ATB_2526_SmartBank.dto.FormationRequest;
import tn.SmartBank.ATB_2526_SmartBank.entity.Formation;
import tn.SmartBank.ATB_2526_SmartBank.repository.FormationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FormationService {

    private final FormationRepository formationRepository;

    @Transactional(readOnly = true)
    public List<Formation> getAll() {
        return formationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Formation getById(Long id) {
        return formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation introuvable avec l'id : " + id));
    }

    public Formation create(FormationRequest request) {
        validate(request);
        Formation formation = Formation.builder()
                .title(request.getTitle())
                .offreFormation(request.getOffreFormation())
                .domain(request.getDomain())
                .theme(request.getTheme())
                .duree(request.getDuree())
                .lieu(request.getLieu())
                .unite(request.getUnite())
                .build();
        return formationRepository.save(formation);
    }

    public Formation update(Long id, FormationRequest request) {
        Formation existing = getById(id);
        validate(request);
        existing.setTitle(request.getTitle());
        existing.setOffreFormation(request.getOffreFormation());
        existing.setDomain(request.getDomain());
        existing.setTheme(request.getTheme());
        existing.setDuree(request.getDuree());
        existing.setLieu(request.getLieu());
        existing.setUnite(request.getUnite());
        return formationRepository.save(existing);
    }

    public void delete(Long id) {
        Formation formation = getById(id);
        formationRepository.delete(formation);
    }

    private void validate(FormationRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Le titre de la formation est obligatoire");
        }
        if (request.getDomain() == null || request.getDomain().isBlank()) {
            throw new IllegalArgumentException("Le domaine de la formation est obligatoire");
        }
        if (request.getDuree() == null || request.getDuree() <= 0) {
            throw new IllegalArgumentException("La durée doit être supérieure à zéro");
        }
    }
}
