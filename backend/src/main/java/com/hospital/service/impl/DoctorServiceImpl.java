package com.hospital.service.impl;

import com.hospital.model.Doctor;
import com.hospital.repository.DoctorRepository;
import com.hospital.service.DoctorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public List<Doctor> listByDept(Long deptId) {
        // 如果 DoctorRepository 还没有这个方法，下面第 4 部分会加
        return doctorRepository.findByDeptId(deptId);
    }
}
