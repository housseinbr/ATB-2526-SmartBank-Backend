package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Donner_Administratif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Donner_AdministratifRepository extends JpaRepository<Donner_Administratif, Long> {
    List<Donner_Administratif> findByUser_Id(Long userId);
}
