package com.hospital.service;

import com.hospital.model.Dept;
import com.hospital.dto.DeptDoctorOverviewDTO;

import java.time.LocalDate;

import java.util.List;

public interface DeptService {

    List<Dept> listAll();
    DeptDoctorOverviewDTO getDeptOverviewWithDoctors(Long deptId, LocalDate date);
}
