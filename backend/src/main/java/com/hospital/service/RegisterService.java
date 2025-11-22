package com.hospital.service;

import com.hospital.dto.RegisterCreateRequest;
import com.hospital.dto.RegisterInfoDTO;
import com.hospital.dto.TodayRegisterListDTO;
import com.hospital.model.RegisterRecord;
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

    // 简单假设：平均每个患者 10 分钟
    private static final int PER_PATIENT_MINUTES = 10;

    public RegisterService(RegisterRepository registerRecordRepository) {
        this.registerRecordRepository = registerRecordRepository;
    }

    /**
     * 创建挂号记录：自动分配 queueNo，并估算前方人数和等待时间
     */
    @Transactional
    public RegisterInfoDTO createRegister(Long userId, RegisterCreateRequest req) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        // ✅ 注意：这里用的是 Desc，和你 Repository 里的一致
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
        record.setStatus("WAITING");
        record.setSource(req.getSource() != null ? req.getSource() : "MANUAL");
        record.setRemark(req.getRemark());

        RegisterRecord saved = registerRecordRepository.save(record);

        // 当前这位前面的人数 = 当前医生今天排队且 queue_no < 当前号的 WAITING 数量
        long ahead = registerRecordRepository
                .countByDoctorIdAndRegisterTimeBetweenAndStatusAndQueueNoLessThan(
                        saved.getDoctorId(),
                        start,
                        end,
                        "WAITING",
                        saved.getQueueNo()
                );
        int aheadCount = (int) Math.max(0, ahead - 1); // 去掉自己

        int estimateWaitMin = aheadCount * PER_PATIENT_MINUTES;

        RegisterInfoDTO dto = toDTO(saved);
        dto.setAheadCount(aheadCount);
        dto.setEstimateWaitMin(estimateWaitMin);
        return dto;
    }

    /**
     * B 区列表：查询当前用户今天的所有挂号记录
     */
    public TodayRegisterListDTO getTodayRegisters(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        // ✅ 这里同样用 Desc，对应 Repository
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

    /**
     * A 区：今天最新一条挂号记录（不区分状态）
     */
    public RegisterInfoDTO getLatestTodayRegister(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<RegisterRecord> list =
                registerRecordRepository.findByUserIdAndRegisterTimeBetweenOrderByRegisterTimeDesc(
                        userId, start, end);

        if (list.isEmpty()) {
            return null;
        }
        RegisterRecord latest = list.get(0);
        return toDTO(latest);
    }

    /**
     * A 区：当前候诊挂号（状态为 WAITING 的最新一条），并计算前方人数和预计等待时间
     */
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

        if (waiting == null) {
            return null;
        }

        // 计算前方等待人数
        long ahead = registerRecordRepository
                .countByDoctorIdAndRegisterTimeBetweenAndStatusAndQueueNoLessThan(
                        waiting.getDoctorId(),
                        start,
                        end,
                        "WAITING",
                        waiting.getQueueNo()
                );
        int aheadCount = (int) Math.max(0, ahead - 1);
        int estimateWaitMin = aheadCount * PER_PATIENT_MINUTES;

        RegisterInfoDTO dto = toDTO(waiting);
        dto.setAheadCount(aheadCount);
        dto.setEstimateWaitMin(estimateWaitMin);
        return dto;
    }

    /**
     * 内部工具：把实体转成 DTO
     */
    private RegisterInfoDTO toDTO(RegisterRecord r) {
        RegisterInfoDTO dto = new RegisterInfoDTO();
        dto.setId(r.getId());
        dto.setPatientId(r.getPatientId());
        dto.setDeptId(r.getDeptId());
        dto.setDoctorId(r.getDoctorId());
        dto.setQueueNo(r.getQueueNo());
        dto.setStatus(r.getStatus());
        dto.setSource(r.getSource());
        dto.setRegisterTime(r.getRegisterTime());
        // deptName / doctorName 先留空，在 HomeOverviewController 里再查库补全
        return dto;
    }
}
