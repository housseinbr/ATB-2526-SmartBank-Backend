package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Bank_Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Bank_AccountRepository extends JpaRepository<Bank_Account, Long> {
}
