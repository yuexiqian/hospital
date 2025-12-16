// src/main/java/com/hospital/service/RegisterService.java
package com.hospital.service;

import com.hospital.dto.RegisterCreateRequest;
import com.hospital.dto.RegisterInfoDTO;
import com.hospital.dto.TodayRegisterListDTO;
import com.hospital.model.Dept;
import com.hospital.model.Doctor;
import com.hospital.model.RegisterRecord;
import com.hospital.repository.DeptRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.RegisterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegisterService {

    private final RegisterRepository registerRecordRepository;
    private final DeptRepository deptRepository;
    private final DoctorRepository doctorRepository;

    private static final int PER_PATIENT_MINUTES = 10;

    public RegisterService(RegisterRepository registerRecordRepository,
                           DeptRepository deptRepository,
                           DoctorRepository doctorRepository) {
        this.registerRecordRepository = registerRecordRepository;
        this.deptRepository = deptRepository;
        this.doctorRepository = doctorRepository;
    }

    /**
     * 创建挂号记录：自动分配 queueNo，并估算前方人数和等待时间
     * ✅ 同时写入 deptName / doctorName，避免新挂号展示为空
     */
    @Transactional
    public RegisterInfoDTO createRegister(Long userId, RegisterCreateRequest req) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<RegisterRecord> todayList =
                registerRecordRepository.findByDoctorIdAndRegisterTimeBetweenOrderByQueueNoDesc(
                        req.getDoctorId(), start, end);

        int nextQueueNo = todayList.size() + 1;

        RegisterRecord record = new RegisterRecord();
        record.setUserId(userId);
        record.setPatientId(req.getPatientId());
        record.setDeptId(req.getDeptId());
        record.setDoctorId(req.getDoctorId());
        record.setQueueNo(nextQueueNo);

        // 业务状态（不是 queue_status）
        record.setStatus("WAITING");
        record.setSource(req.getSource() != null ? req.getSource() : "MANUAL");
        record.setRemark(req.getRemark());

        // ✅ 显式补齐队列字段（更稳）
        record.setQueueStatus(0);
        record.setQueuePriority(0);
        record.setCalledTimes(0);

        // ✅ 填充展示字段（关键）
        if (req.getDeptId() != null) {
            deptRepository.findById(req.getDeptId())
                    .map(Dept::getName)
                    .ifPresent(record::setDeptName);
        }
        if (req.getDoctorId() != null) {
            doctorRepository.findById(req.getDoctorId())
                    .map(Doctor::getName)
                    .ifPresent(record::setDoctorName);
        }

        RegisterRecord saved = registerRecordRepository.save(record);

        long ahead = registerRecordRepository
                .countByDoctorIdAndRegisterTimeBetweenAndStatusAndQueueNoLessThan(
                        saved.getDoctorId(), start, end, "WAITING", saved.getQueueNo()
                );

        int aheadCount = (int) Math.max(0, ahead - 1);
        int estimateWaitMin = aheadCount * PER_PATIENT_MINUTES;

        RegisterInfoDTO dto = toDTO(saved);
        dto.setAheadCount(aheadCount);
        dto.setEstimateWaitMin(estimateWaitMin);
        return dto;
    }

    public TodayRegisterListDTO getTodayRegisters(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<RegisterRecord> list =
                registerRecordRepository.findByUserIdAndRegisterTimeBetweenOrderByRegisterTimeDesc(
                        userId, start, end);

        List<RegisterInfoDTO> items = list.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        TodayRegisterListDTO dto = new TodayRegisterListDTO();
        dto.setCount(items.size());
        dto.setItems(items);
        return dto;
    }

    public RegisterInfoDTO getLatestTodayRegister(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<RegisterRecord> list =
                registerRecordRepository.findByUserIdAndRegisterTimeBetweenOrderByRegisterTimeDesc(
                        userId, start, end);

        if (list.isEmpty()) return null;
        return toDTO(list.get(0));
    }

    public RegisterInfoDTO getLatestTodayRegisterByPatient(Long userId, Long patientId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<RegisterRecord> list =
                registerRecordRepository.findByUserIdAndPatientIdAndRegisterTimeBetweenOrderByRegisterTimeDesc(
                        userId, patientId, start, end);

        if (list.isEmpty()) return null;
        return toDTO(list.get(0));
    }

    public RegisterInfoDTO getLatestWaitingTodayRegister(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<RegisterRecord> list =
                registerRecordRepository.findByUserIdAndRegisterTimeBetweenOrderByRegisterTimeDesc(
                        userId, start, end);

        RegisterRecord waiting = list.stream()
                .filter(r -> "WAITING".equalsIgnoreCase(r.getStatus()))
                .findFirst()
                .orElse(null);

        if (waiting == null) return null;

        long ahead = registerRecordRepository
                .countByDoctorIdAndRegisterTimeBetweenAndStatusAndQueueNoLessThan(
                        waiting.getDoctorId(), start, end, "WAITING", waiting.getQueueNo()
                );

        int aheadCount = (int) Math.max(0, ahead - 1);
        int estimateWaitMin = aheadCount * PER_PATIENT_MINUTES;

        RegisterInfoDTO dto = toDTO(waiting);
        dto.setAheadCount(aheadCount);
        dto.setEstimateWaitMin(estimateWaitMin);
        return dto;
    }

    public RegisterInfoDTO getLatestWaitingTodayRegisterByPatient(Long userId, Long patientId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<RegisterRecord> list =
                registerRecordRepository.findByUserIdAndPatientIdAndRegisterTimeBetweenOrderByRegisterTimeDesc(
                        userId, patientId, start, end);

        RegisterRecord waiting = list.stream()
                .filter(r -> "WAITING".equalsIgnoreCase(r.getStatus()))
                .findFirst()
                .orElse(null);

        if (waiting == null) return null;

        long ahead = registerRecordRepository
                .countByDoctorIdAndRegisterTimeBetweenAndStatusAndQueueNoLessThan(
                        waiting.getDoctorId(), start, end, "WAITING", waiting.getQueueNo()
                );

        int aheadCount = (int) Math.max(0, ahead - 1);
        int estimateWaitMin = aheadCount * PER_PATIENT_MINUTES;

        RegisterInfoDTO dto = toDTO(waiting);
        dto.setAheadCount(aheadCount);
        dto.setEstimateWaitMin(estimateWaitMin);
        return dto;
    }

    private RegisterInfoDTO toDTO(RegisterRecord r) {
        RegisterInfoDTO dto = new RegisterInfoDTO();
        dto.setId(r.getId());
        dto.setUserId(r.getUserId());
        dto.setPatientId(r.getPatientId());
        dto.setDeptId(r.getDeptId());
        dto.setDoctorId(r.getDoctorId());
        dto.setQueueNo(r.getQueueNo());
        dto.setStatus(r.getStatus());
        dto.setSource(r.getSource());
        dto.setRegisterTime(r.getRegisterTime());
        dto.setRemark(r.getRemark());
        return dto;
    }
}
