package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.Enums.Status;
import tn.SmartBank.ATB_2526_SmartBank.entity.Abcence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbcenceRepository extends JpaRepository<Abcence, Long> {

    List<Abcence> findByUser_Id(Long userId);

    List<Abcence> findByStatus(Status status);

    List<Abcence> findByUser_IdAndStatus(Long userId, Status status);

    List<Abcence> findByUser_Superviseur_Id(Long superviseurId);

    List<Abcence> findByUser_Superviseur_IdAndStatus(Long superviseurId, Status status);

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

    @Query("""
        SELECT a FROM Abcence a
        WHERE a.user.id = :userId
        AND a.status = :status
        AND a.dateStart <= :dateEnd
        AND a.dateEnd >= :dateStart
        """)
    List<Abcence> findApprovedOverlapping(
            @Param("userId") Long userId,
            @Param("dateStart") LocalDate dateStart,
            @Param("dateEnd") LocalDate dateEnd,
            @Param("status") Status status
    );

    @Query("""
        SELECT a FROM Abcence a
        WHERE a.user.id = :userId
        AND a.idAbcance <> :excludeId
        AND a.status = :status
        AND a.dateStart <= :dateEnd
        AND a.dateEnd >= :dateStart
        """)
    List<Abcence> findApprovedOverlappingExcludingSelf(
            @Param("userId") Long userId,
            @Param("dateStart") LocalDate dateStart,
            @Param("dateEnd") LocalDate dateEnd,
            @Param("excludeId") Long excludeId,
            @Param("status") Status status
    );
}
