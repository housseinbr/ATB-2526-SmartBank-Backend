package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.Bank_Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Bank_AccountRepository extends JpaRepository<Bank_Account, Long> {
    Optional<Bank_Account> findByUser_Id(Long userId);
}
