package com.hospital.repository;

import com.hospital.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {


    List<Doctor> findByDeptId(Long deptId);
    List<Doctor> findByDeptIdAndStatus(Long deptId, Integer status);
    Optional<Doctor> findByPhone(String phone);
    Optional<Doctor> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
