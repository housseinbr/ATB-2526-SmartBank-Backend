package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.entity.Demande_Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Demande_FormationRepository extends JpaRepository<Demande_Formation, Long> {
    List<Demande_Formation> findByUser_IdOrderByDateDesc(Long userId);

    List<Demande_Formation> findByUser_Superviseur_IdOrderByDateDesc(Long supervisorId);

    List<Demande_Formation> findByUser_Superviseur_IdAndStatusOrderByDateDesc(Long supervisorId, Status status);

    List<Demande_Formation> findAllByOrderByDateDesc();

    boolean existsByUser_IdAndFormation_IdFormationAndStatusIn(Long userId, Long formationId, List<Status> statuses);

    Optional<Demande_Formation> findByIdDemandeFormation(Long idDemandeFormation);
}
