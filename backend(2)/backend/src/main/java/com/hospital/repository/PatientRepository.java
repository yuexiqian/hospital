package com.hospital.repository;

import com.hospital.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Collection;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * 查询某个用户的全部就诊人
     */
    List<Patient> findByPatientIdIn(Collection<Long> ids);
    List<Patient> findByUserId(Long userId);
}
