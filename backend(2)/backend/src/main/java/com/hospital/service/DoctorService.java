// src/main/java/com/hospital/service/DoctorService.java
package com.hospital.service;

import com.hospital.dto.*;
import com.hospital.model.Doctor;

import java.time.LocalDate;
import java.util.List;

public interface DoctorService {

    // 挂号时按科室查医生
    List<Doctor> listByDept(Long deptId);

    // 医生当天候诊队列
    List<DoctorQueueItemDTO> getQueue(Long doctorId, LocalDate date);

    // 某次挂号的就诊详情
    DoctorVisitDetailDTO getVisitDetail(Long registerId);

    // 开始就诊（队列状态 -> 就诊中）
    void startVisit(Long registerId);

    // 结束就诊（队列状态 -> 已完成）
    void finishVisit(Long registerId);

    // 保存病历 + 处方
    void saveVisit(Long registerId, DoctorVisitSaveRequest req);

    // 患者历史就诊记录
    List<DoctorVisitHistoryItemDTO> getPatientHistory(Long patientId);

    // 医生端药品搜索
    List<DoctorDrugSimpleDTO> searchDrugs(String keyword);
}
