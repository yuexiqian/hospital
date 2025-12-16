package com.hospital.repository;

import com.hospital.model.DrugInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 药品库存表 Repository
 */
public interface DrugInventoryRepository extends JpaRepository<DrugInventory, Long> {

    /**
     * 查某个药品的所有库存记录，按 id 正序
     */
    List<DrugInventory> findByDrugIdOrderByIdAsc(Long drugId);

    /**
     * 发药逻辑用：拿该药品的一条库存记录（最早的一条）
     */
    Optional<DrugInventory> findFirstByDrugIdOrderByIdAsc(Long drugId);
}
