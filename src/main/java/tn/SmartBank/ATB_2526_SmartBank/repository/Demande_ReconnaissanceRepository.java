package tn.SmartBank.ATB_2526_SmartBank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande_Reconnaissance;

import java.util.List;
import java.util.Optional;

@Repository
public interface Demande_ReconnaissanceRepository extends JpaRepository<Demande_Reconnaissance, Long> {
    List<Demande_Reconnaissance> findByUser_IdOrderByDateDesc(Long userId);
    List<Demande_Reconnaissance> findByUser_Superviseur_IdOrderByDateDesc(Long superviseurId);
    List<Demande_Reconnaissance> findByUser_Superviseur_IdAndStatusOrderByDateDesc(Long superviseurId, Status status);
    Optional<Demande_Reconnaissance> findByIdDemandeReconnaissance(Long id);
}
