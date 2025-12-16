// src/main/java/com/hospital/repository/PrescriptionRepository.java
package com.hospital.repository;

import com.hospital.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // 按日期范围查（当天：start = 00:00:00, end = 23:59:59）
    List<Prescription> findByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    // 按日期范围 + 状态查
    List<Prescription> findByCreateTimeBetweenAndStatus(LocalDateTime start,
                                                        LocalDateTime end,
                                                        String status);
    List<Prescription> findByRegisterIdOrderByIdAsc(Long registerId);
    Optional<Prescription> findFirstByRegisterIdOrderByIdAsc(Long registerId);


    // === 新增：根据挂号记录查该次就诊的所有处方 ===
    List<Prescription> findByRegisterId(Long registerId);
}
