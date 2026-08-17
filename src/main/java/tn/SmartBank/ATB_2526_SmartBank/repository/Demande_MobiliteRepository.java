package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Demande_Mobilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface Demande_MobiliteRepository extends JpaRepository<Demande_Mobilite, Long> {
    List<Demande_Mobilite> findByUser_IdOrderByDateDesc(Long userId);
    List<Demande_Mobilite> findByUser_Superviseur_IdOrderByDateDesc(Long superviseurId);
    List<Demande_Mobilite> findByUser_Superviseur_IdAndStatusOrderByDateDesc(Long superviseurId, Status status);
    Optional<Demande_Mobilite> findByIdDemande(Long idDemande);
}
