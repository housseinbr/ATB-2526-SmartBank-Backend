package tn.SmartBank.ATB_2526_SmartBank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.SmartBank.ATB_2526_SmartBank.entity.Document_Reconnaissance;

import java.util.Optional;

@Repository
public interface Document_ReconnaissanceRepository extends JpaRepository<Document_Reconnaissance, Long> {
    Optional<Document_Reconnaissance> findByDemande_IdDemandeReconnaissance(Long demandeId);
}
