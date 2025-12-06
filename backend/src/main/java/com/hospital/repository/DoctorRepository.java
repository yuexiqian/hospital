package com.hospital.repository;

import com.hospital.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByDeptId(Long deptId);
    List<Doctor> findByDeptIdAndStatus(Long deptId, Integer status);
}
