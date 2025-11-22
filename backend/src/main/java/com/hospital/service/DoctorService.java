package com.hospital.service;

import com.hospital.model.Doctor;

import java.util.List;

public interface DoctorService {

    List<Doctor> listByDept(Long deptId);
}
