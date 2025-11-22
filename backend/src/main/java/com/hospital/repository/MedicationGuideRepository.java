package com.hospital.repository;

import com.hospital.model.MedicationGuide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicationGuideRepository extends JpaRepository<MedicationGuide, Long> {

    /** 按用户取最新一条用药指导 */
    Optional<MedicationGuide> findFirstByUserIdOrderByCreateTimeDesc(Long userId);
}
