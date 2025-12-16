package com.hospital.repository;

import com.hospital.model.BillDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillDetailRepository extends JpaRepository<BillDetail, Long> {

    /**
     * 按 billId 查询该费用下的所有明细
     */
    List<BillDetail> findByBillId(Long billId);
}
