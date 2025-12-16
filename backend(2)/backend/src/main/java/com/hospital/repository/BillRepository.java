package com.hospital.repository;

import com.hospital.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    // ========= 你原来的 =========

    long countByUserIdAndStatus(Long userId, String status);

    @Query("select coalesce(sum(b.amount), 0) from Bill b where b.userId = :userId and b.status = :status")
    BigDecimal sumAmountByUserIdAndStatus(Long userId, String status);

    // ========= 列表查询用 =========

    /** 待缴列表：按创建时间倒序 */
    List<Bill> findByUserIdAndStatusOrderByCreateTimeDesc(Long userId, String status);

    /** 历史列表：按支付时间倒序 */
    List<Bill> findByUserIdAndStatusOrderByPaidTimeDesc(Long userId, String status);

    // ========= ✅ 新增：用于“处方缴费”账单 upsert，避免重复生成 =========
    Optional<Bill> findFirstByUserIdAndStatusAndItemName(Long userId, String status, String itemName);
}
