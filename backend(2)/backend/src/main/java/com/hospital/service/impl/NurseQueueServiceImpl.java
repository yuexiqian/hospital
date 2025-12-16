// src/main/java/com/hospital/service/impl/NurseQueueServiceImpl.java
package com.hospital.service.impl;

import com.hospital.dto.NurseQueueItemDTO;
import com.hospital.dto.NurseTriageRequest;
import com.hospital.model.Dept;
import com.hospital.model.Doctor;
import com.hospital.model.RegisterRecord;
import com.hospital.repository.RegisterRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.DeptRepository;
import com.hospital.service.NurseQueueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NurseQueueServiceImpl implements NurseQueueService {

    private final RegisterRepository registerRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DeptRepository deptRepository;

    public NurseQueueServiceImpl(RegisterRepository registerRepository,
                                 PatientRepository patientRepository,
                                 DoctorRepository doctorRepository,
                                 DeptRepository deptRepository) {
        this.registerRepository = registerRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.deptRepository = deptRepository;
    }

    @Override
    public List<NurseQueueItemDTO> listQueue(Long deptId,
                                             Long doctorId,
                                             LocalDate visitDate,
                                             Integer queueStatus) {

        if (visitDate == null) visitDate = LocalDate.now();
        LocalDateTime start = visitDate.atStartOfDay();
        LocalDateTime end = visitDate.plusDays(1).atStartOfDay();

        List<RegisterRecord> list = registerRepository.findQueueList(
                deptId, doctorId, start, end, queueStatus
        );

        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    private NurseQueueItemDTO toDto(RegisterRecord r) {
        NurseQueueItemDTO dto = new NurseQueueItemDTO();
        dto.setId(r.getId());

        // 患者
        dto.setPatientId(r.getPatientId());
        if (r.getPatientId() != null) {
            patientRepository.findById(r.getPatientId())
                    .ifPresent(p -> dto.setPatientName(p.getName()));
        }

        // 科室（优先用 register_record.dept_name；为空再查 dept 表；仍为空给 '-'）
        dto.setDeptId(r.getDeptId());
        String deptName = r.getDeptName();
        if ((deptName == null || deptName.isBlank()) && r.getDeptId() != null) {
            deptName = deptRepository.findById(r.getDeptId())
                    .map(Dept::getName)
                    .orElse("-");
        }
        dto.setDeptName((deptName == null || deptName.isBlank()) ? "-" : deptName);

        // 医生（优先用 register_record.doctor_name；为空再查 doctor 表；仍为空给 '-'）
        dto.setDoctorId(r.getDoctorId());
        String doctorName = r.getDoctorName();
        if ((doctorName == null || doctorName.isBlank()) && r.getDoctorId() != null) {
            doctorName = doctorRepository.findById(r.getDoctorId())
                    .map(Doctor::getName)
                    .orElse("-");
        }
        dto.setDoctorName((doctorName == null || doctorName.isBlank()) ? "-" : doctorName);

        // 其它字段
        dto.setRegisterTime(r.getRegisterTime());
        dto.setQueueStatus(r.getQueueStatus() == null ? 0 : r.getQueueStatus());
        dto.setQueuePriority(r.getQueuePriority() == null ? 0 : r.getQueuePriority());
        dto.setCalledTimes(r.getCalledTimes() == null ? 0 : r.getCalledTimes());
        dto.setLastCallTime(r.getLastCallTime());
        dto.setTriageNurseId(r.getTriageNurseId());
        dto.setTriageTime(r.getTriageTime());
        dto.setTriageNote(r.getTriageNote());
        return dto;
    }

    @Override
    public void callPatient(Long registerId, Long nurseId) {
        RegisterRecord record = registerRepository.findById(registerId)
                .orElseThrow(() -> new RuntimeException("叫号失败：挂号记录不存在"));

        Integer qs = record.getQueueStatus();
        if (qs == null) qs = 0;

        // 只允许 0(候诊)/1(已叫号) 继续叫号
        if (qs != 0 && qs != 1) {
            throw new RuntimeException("叫号失败：当前状态不允许叫号");
        }

        LocalDateTime now = LocalDateTime.now();
        record.setQueueStatus(1);
        record.setLastCallTime(now);
        record.setCalledTimes((record.getCalledTimes() == null ? 0 : record.getCalledTimes()) + 1);

        record.setTriageNurseId(nurseId);
        if (record.getTriageTime() == null) {
            record.setTriageTime(now);
        }

        registerRepository.save(record);
    }

    @Override
    public void markNoShow(Long registerId) {
        RegisterRecord record = registerRepository.findById(registerId)
                .orElseThrow(() -> new RuntimeException("标记过号失败：记录不存在"));
        record.setQueueStatus(4);
        registerRepository.save(record);
    }

    @Override
    public void setPriority(Long registerId, Integer priority) {
        RegisterRecord record = registerRepository.findById(registerId)
                .orElseThrow(() -> new RuntimeException("设置加急失败：记录不存在"));
        record.setQueuePriority(priority == null ? 100 : priority);
        registerRepository.save(record);
    }

    @Override
    public void cancelPriority(Long registerId) {
        RegisterRecord record = registerRepository.findById(registerId)
                .orElseThrow(() -> new RuntimeException("取消加急失败：记录不存在"));
        record.setQueuePriority(0);
        registerRepository.save(record);
    }

    @Override
    public void saveTriageInfo(Long registerId, NurseTriageRequest req) {
        if (req == null || req.getNurseId() == null) {
            throw new IllegalArgumentException("分诊护士ID不能为空");
        }
        RegisterRecord record = registerRepository.findById(registerId)
                .orElseThrow(() -> new RuntimeException("保存分诊信息失败：记录不存在"));

        record.setTriageNurseId(req.getNurseId());
        record.setTriageNote(req.getTriageNote());
        record.setTriageTime(LocalDateTime.now());

        registerRepository.save(record);
    }
}
