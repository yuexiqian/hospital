package com.hospital.service.impl;

import com.hospital.model.Patient;
import com.hospital.repository.PatientRepository;
import com.hospital.service.PatientService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> listByUser(Long userId) {
        return patientRepository.findByUserId(userId);
    }

    @Override
    public Patient createPatient(Long userId, Patient patient) {
        // 确保是新增
        patient.setPatientId(null);
        patient.setUserId(userId);
        patient.setCreateTime(LocalDateTime.now());
        return patientRepository.save(patient);
    }

    @Override
    public void deletePatient(Long userId, Long patientId) {
        Patient p = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("就诊人不存在"));
        if (!p.getUserId().equals(userId)) {
            throw new RuntimeException("不能删除他人的就诊人");
        }
        patientRepository.delete(p);
    }

    @Override
    public Patient updatePatient(Long userId, Long patientId, Patient patient) {
        // 先查出原来的记录
        Patient db = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("就诊人不存在"));

        // 校验是否属于当前用户
        if (!db.getUserId().equals(userId)) {
            throw new RuntimeException("不能修改他人的就诊人");
        }

        // 更新需要允许修改的字段（根据你的实体字段调整）
        db.setName(patient.getName());
        db.setIdType(patient.getIdType());
        db.setIdCard(patient.getIdCard());
        db.setPhone(patient.getPhone());
        // 如果有 updateTime 字段，可以顺便改一下
        // db.setUpdateTime(LocalDateTime.now());

        return patientRepository.save(db);
    }
}
