package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Familly_situation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Familly_situationRepository extends JpaRepository<Familly_situation, Long> {
    Optional<Familly_situation> findByUser_Id(Long userId);
}
