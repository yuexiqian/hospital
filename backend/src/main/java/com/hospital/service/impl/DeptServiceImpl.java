package com.hospital.service.impl;

import com.hospital.model.Dept;
import com.hospital.repository.DeptRepository;
import com.hospital.service.DeptService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    private final DeptRepository deptRepository;

    public DeptServiceImpl(DeptRepository deptRepository) {
        this.deptRepository = deptRepository;
    }

    @Override
    public List<Dept> listAll() {
        // 如需按 status 过滤，可在这里加条件
        return deptRepository.findAll();
    }
}
