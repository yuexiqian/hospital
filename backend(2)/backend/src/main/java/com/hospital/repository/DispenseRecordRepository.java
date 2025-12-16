// src/main/java/com/hospital/repository/DispenseRecordRepository.java
package com.hospital.repository;

import com.hospital.model.DispenseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispenseRecordRepository extends JpaRepository<DispenseRecord, Long> {
}
