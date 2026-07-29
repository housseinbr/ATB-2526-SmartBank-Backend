package tn.SmartBank.ATB_2526_SmartBank.repository;

import tn.SmartBank.ATB_2526_SmartBank.entity.History_Sold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface History_SoldRepository extends JpaRepository<History_Sold, Long> {
    List<History_Sold> findByUser_IdOrderByDateActionDesc(Long userId);
}
