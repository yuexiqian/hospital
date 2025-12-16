// src/main/java/com/hospital/repository/PrescriptionItemRepository.java
package com.hospital.repository;

import com.hospital.model.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 处方明细表 Repository
 */
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long> {

    /**
     * 查某张处方下面的所有明细
     */
    List<PrescriptionItem> findByPrescriptionId(Long prescriptionId);

    /**
     * 删除某张处方下的所有明细（更新处方时先删再插）
     */
    void deleteByPrescriptionId(Long prescriptionId);
}
