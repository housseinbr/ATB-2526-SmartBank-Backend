package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByUser_IdOrderByDateDesc(Long userId);
    List<Evaluation> findBySuperviseur_IdOrderByDateDesc(Long superviseurId);
    List<Evaluation> findBySuperviseur_IdAndStatusOrderByDateDesc(Long superviseurId, Status status);
}
