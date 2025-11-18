package com.hospital.repository;

import com.hospital.model.Patient;
import com.hospital.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByOwner(User owner);
}

