package com.hospital.service;

import com.hospital.model.Patient;

import java.util.List;

public interface PatientService {

    /**
     * 查询某个用户的全部就诊人
     */
    List<Patient> listByUser(Long userId);

    /**
     * 新增就诊人
     */
    Patient createPatient(Long userId, Patient patient);

    /**
     * 删除就诊人
     */
    void deletePatient(Long userId, Long patientId);

    /**
     * 修改就诊人信息
     */
    Patient updatePatient(Long userId, Long patientId, Patient patient);
}
