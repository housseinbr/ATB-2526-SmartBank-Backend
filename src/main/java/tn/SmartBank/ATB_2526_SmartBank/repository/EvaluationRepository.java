package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}
