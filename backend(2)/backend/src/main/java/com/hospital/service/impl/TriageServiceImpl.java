package com.hospital.service.impl;

import com.hospital.dto.DeptQueueDTO;
import com.hospital.dto.DoctorSimpleDTO;
import com.hospital.dto.TriageRequest;
import com.hospital.dto.TriageResultDTO;
import com.hospital.model.Dept;
import com.hospital.model.Doctor;
import com.hospital.model.RegisterRecord;
import com.hospital.repository.DeptRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.RegisterRepository;
import com.hospital.service.TriageService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TriageServiceImpl implements TriageService {

    private final DeptRepository deptRepository;
    private final DoctorRepository doctorRepository;
    private final RegisterRepository registerRepository;

    public TriageServiceImpl(DeptRepository deptRepository,
                             DoctorRepository doctorRepository,
                             RegisterRepository registerRepository) {
        this.deptRepository = deptRepository;
        this.doctorRepository = doctorRepository;
        this.registerRepository = registerRepository;
    }

    @Override
    public TriageResultDTO advise(TriageRequest request) {
        if (request == null || !StringUtils.hasText(request.getBodyPart())) {
            throw new RuntimeException("请先选择不适部位");
        }

        // 1. 根据部位+症状得到推荐科室 code
        String mainDeptCode = recommendDeptCode(request.getBodyPart(), request.getSymptoms());
        // 默认备选，全科门诊（如果有）
        String backupDeptCode = "GP";

        // 2. 查找 Dept 实体
        Dept mainDept = deptRepository.findByCode(mainDeptCode)
                .orElseThrow(() -> new RuntimeException("找不到推荐科室:" + mainDeptCode));

        Dept backupDept = null;
        if (!Objects.equals(mainDeptCode, backupDeptCode)) {
            backupDept = deptRepository.findByCode(backupDeptCode).orElse(null);
        }

        // 3. 计算今日时间范围
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        // 4. 组装科室候诊情况
        List<DeptQueueDTO> queueList = new ArrayList<>();
        queueList.add(buildDeptQueue(mainDept, start, end));
        if (backupDept != null) {
            queueList.add(buildDeptQueue(backupDept, start, end));
        }

        // 5. 推荐医生列表（主科室下所有医生）
        List<Doctor> doctors = doctorRepository.findByDeptId(mainDept.getId());
        List<DoctorSimpleDTO> doctorDTOList = doctors.stream()
                .map(d -> {
                    DoctorSimpleDTO dto = new DoctorSimpleDTO();
                    dto.setDoctorId(d.getId());
                    dto.setDoctorName(d.getName());
                    dto.setTitle(d.getTitle());
                    long waitCount = registerRepository.countByDoctorIdAndRegisterTimeBetweenAndStatus(
                            d.getId(), start, end, "WAITING");
                    dto.setWaitCount(waitCount);
                    return dto;
                })
                .collect(Collectors.toList());

        // 6. 推荐理由简单生成一下
        String reason = buildReasonText(request, mainDept);

        // 7. 组装结果
        TriageResultDTO result = new TriageResultDTO();
        result.setMainDeptId(mainDept.getId());
        result.setMainDeptName(mainDept.getName());

        if (backupDept != null) {
            result.setBackupDeptId(backupDept.getId());
            result.setBackupDeptName(backupDept.getName());
        }

        result.setReason(reason);
        result.setQueueInfo(queueList);
        result.setDoctors(doctorDTOList);

        return result;
    }

    /**
     * 最简单的规则：根据部位 + 关键症状映射到科室 code
     * 你库里已有的 dept.code 里，请确保有：RESP, GI, NEURO, ORTHO, DERM, GP 等。
     * 没有就至少保证 RESP, GI, GP 这几个。
     */
    private String recommendDeptCode(String bodyPart, List<String> symptoms) {
        if (symptoms == null) symptoms = Collections.emptyList();
        List<String> lower = symptoms.stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        String bp = bodyPart.toLowerCase();

        if (bp.contains("胸")) {
            if (containsAny(lower, "咳", "喘", "呼吸", "胸闷", "气短", "发热")) {
                return "RESP"; // 呼吸内科
            }
        }
        if (bp.contains("腹") || bp.contains("胃")) {
            return "GI"; // 消化内科
        }
        if (bp.contains("头") || bp.contains("脑")) {
            return "NEURO"; // 神经内科
        }
        if (bp.contains("骨") || bp.contains("四肢") || bp.contains("关节")) {
            return "ORTHO"; // 骨科
        }
        if (bp.contains("皮") || containsAny(lower, "皮疹", "瘙痒", "过敏")) {
            return "DERM"; // 皮肤科
        }

        // 默认全科门诊
        return "GP";
    }

    private boolean containsAny(List<String> list, String... keys) {
        for (String key : keys) {
            for (String s : list) {
                if (s.contains(key.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private DeptQueueDTO buildDeptQueue(Dept dept, LocalDateTime start, LocalDateTime end) {
        DeptQueueDTO dto = new DeptQueueDTO();
        dto.setDeptId(dept.getId());
        dto.setDeptName(dept.getName());

        long waitCount = registerRepository.countByDeptIdAndRegisterTimeBetweenAndStatus(
                dept.getId(), start, end, "WAITING");
        dto.setWaitCount(waitCount);
        dto.setEstimateWaitMin(waitCount * 10); // 每人假设10分钟

        return dto;
    }

    private String buildReasonText(TriageRequest req, Dept mainDept) {
        StringBuilder sb = new StringBuilder("根据");
        sb.append("不适部位【").append(req.getBodyPart()).append("】");

        if (!CollectionUtils.isEmpty(req.getSymptoms())) {
            sb.append("，症状【").append(String.join("、", req.getSymptoms())).append("】");
        }
        if (StringUtils.hasText(req.getSeverity())) {
            sb.append("，病情程度【").append(req.getSeverity()).append("】");
        }
        sb.append("，推荐科室：").append(mainDept.getName()).append("。");
        return sb.toString();
    }
}
