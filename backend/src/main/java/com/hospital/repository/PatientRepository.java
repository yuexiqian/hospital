package com.hospital.repository;

import com.hospital.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * 查询某个用户的全部就诊人
     */
    List<Patient> findByUserId(Long userId);
}
