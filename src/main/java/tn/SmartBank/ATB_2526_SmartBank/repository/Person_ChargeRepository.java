package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Person_Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Person_ChargeRepository extends JpaRepository<Person_Charge, Long> {
}
