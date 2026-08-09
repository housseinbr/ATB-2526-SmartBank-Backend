package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.SmartBank.ATB_2526_SmartBank.entity.Competance;
import tn.SmartBank.ATB_2526_SmartBank.entity.Formation;
import tn.SmartBank.ATB_2526_SmartBank.entity.User;
import tn.SmartBank.ATB_2526_SmartBank.repository.CompetanceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompetanceService {

    private final CompetanceRepository competanceRepository;

    @Transactional(readOnly = true)
    public List<Competance> getMyCompetances(Long userId) {
        return competanceRepository.findByUser_IdOrderByFormation_TitleAsc(userId);
    }

    @Transactional(readOnly = true)
    public List<Competance> getUserCompetances(Long userId) {
        return competanceRepository.findByUser_IdOrderByFormation_TitleAsc(userId);
    }

    @Transactional(readOnly = true)
    public boolean existsForUserAndFormation(Long userId, Long formationId) {
        return competanceRepository.existsByUser_IdAndFormation_IdFormation(userId, formationId);
    }

    public Competance createIfAbsent(User user, Formation formation) {
        return competanceRepository.findByUser_IdAndFormation_IdFormation(user.getId(), formation.getIdFormation())
                .orElseGet(() -> competanceRepository.save(Competance.builder()
                        .user(user)
                        .formation(formation)
                        .build()));
    }

    public void delete(Long id) {
        Competance competance = competanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compétence introuvable avec l'id : " + id));
        competanceRepository.delete(competance);
    }
}
