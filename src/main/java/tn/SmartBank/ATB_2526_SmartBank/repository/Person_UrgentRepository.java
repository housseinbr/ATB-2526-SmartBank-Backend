package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Person_Urgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Person_UrgentRepository extends JpaRepository<Person_Urgent, Long> {
    List<Person_Urgent> findByUser_Id(Long userId);
}
