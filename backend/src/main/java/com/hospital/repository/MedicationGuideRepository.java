package com.hospital.repository;

import com.hospital.model.MedicationGuide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicationGuideRepository extends JpaRepository<MedicationGuide, Long> {

    /** 首页用：按用户取最新一条用药指导 */
    Optional<MedicationGuide> findFirstByUserIdOrderByCreateTimeDesc(Long userId);

    /** 用药页面用：按用户取全部用药指导，时间倒序 */
    List<MedicationGuide> findByUserIdOrderByCreateTimeDesc(Long userId);
}
