package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {
}
