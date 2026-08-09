package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Competance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetanceRepository extends JpaRepository<Competance, Long> {
    List<Competance> findByUser_IdOrderByFormation_TitleAsc(Long userId);

    Optional<Competance> findByUser_IdAndFormation_IdFormation(Long userId, Long formationId);

    boolean existsByUser_IdAndFormation_IdFormation(Long userId, Long formationId);
}
