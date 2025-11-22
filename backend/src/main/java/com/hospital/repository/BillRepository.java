package com.hospital.repository;

import com.hospital.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface BillRepository extends JpaRepository<Bill, Long> {

    long countByUserIdAndStatus(Long userId, String status);

    @Query("select coalesce(sum(b.amount), 0) from Bill b where b.userId = :userId and b.status = :status")
    BigDecimal sumAmountByUserIdAndStatus(Long userId, String status);
}
