package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Abcence;
import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbcenceRepository extends JpaRepository<Abcence, Long> {

    // Toutes les absences d'un utilisateur
    List<Abcence> findByUser_Id(Long userId);

    // Toutes les absences ayant un statut donné (ex: EN_ATTENTE pour la file de validation du superviseur)
    List<Abcence> findByStatus(Status status);

    // Absences d'un utilisateur filtrées par statut
    List<Abcence> findByUser_IdAndStatus(Long userId, Status status);

    // Vérifie si l'utilisateur a déjà une absence qui chevauche [dateStart, dateEnd]
    @Query("""
        SELECT a FROM Abcence a
        WHERE a.user.id = :userId
        AND a.status <> :excludedStatus
        AND a.dateStart <= :dateEnd
        AND a.dateEnd >= :dateStart
        """)
    List<Abcence> findOverlapping(
            @Param("userId") Long userId,
            @Param("dateStart") LocalDate dateStart,
            @Param("dateEnd") LocalDate dateEnd,
            @Param("excludedStatus") Status excludedStatus
    );

    // Idem, en excluant l'enregistrement courant (utile pour l'UPDATE)
    @Query("""
        SELECT a FROM Abcence a
        WHERE a.user.id = :userId
        AND a.idAbcance <> :excludeId
        AND a.status <> :excludedStatus
        AND a.dateStart <= :dateEnd
        AND a.dateEnd >= :dateStart
        """)
    List<Abcence> findOverlappingExcludingSelf(
            @Param("userId") Long userId,
            @Param("dateStart") LocalDate dateStart,
            @Param("dateEnd") LocalDate dateEnd,
            @Param("excludeId") Long excludeId,
            @Param("excludedStatus") Status excludedStatus
    );
}