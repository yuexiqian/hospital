package com.hospital.service.impl;

import com.hospital.dto.DeptDetailDTO;
import com.hospital.dto.DeptDoctorOverviewDTO;
import com.hospital.dto.DoctorSlotDTO;
import com.hospital.model.Dept;
import com.hospital.model.Doctor;
import com.hospital.repository.DeptRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.RegisterRepository;
import com.hospital.service.DeptService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DeptServiceImpl implements DeptService {

    private final DeptRepository deptRepository;
    private final DoctorRepository doctorRepository;
    private final RegisterRepository registerRepository;

    public DeptServiceImpl(DeptRepository deptRepository,
                           DoctorRepository doctorRepository,
                           RegisterRepository registerRepository) {
        this.deptRepository = deptRepository;
        this.doctorRepository = doctorRepository;
        this.registerRepository = registerRepository;
    }

    @Override
    public List<Dept> listAll() {
        // 如需按 status 过滤，可在这里改成 findByStatus(1)
        return deptRepository.findAll();
    }

    @Override
    public DeptDoctorOverviewDTO getDeptOverviewWithDoctors(Long deptId, LocalDate date) {
        Dept dept = deptRepository.findById(deptId)
                .orElseThrow(() -> new RuntimeException("科室不存在"));

        // 1. 科室详情 DTO
        DeptDetailDTO deptDTO = toDeptDetailDTO(dept);

        // 2. 该科室下所有启用医生（status=1）
        List<Doctor> doctors = doctorRepository.findByDeptIdAndStatus(deptId, 1);

        // 3. 当天起止时间
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.plusDays(1).atStartOfDay();

        // 4. 哪些状态算“占用号源”
        // TODO: 按你真实的枚举来，比如 WAITING / CALLED / IN_PROGRESS / FINISHED ...
        Set<String> validStatus = Set.of("WAITING", "IN_QUEUE", "FINISHED");

        // 5. 组装医生介绍 + 当日号源情况
        List<DoctorSlotDTO> doctorDTOs = doctors.stream().map(d -> {
            DoctorSlotDTO dto = new DoctorSlotDTO();
            dto.setId(d.getId());
            dto.setName(d.getName());
            dto.setGender(d.getGender());
            dto.setTitle(d.getTitle());
            dto.setDeptId(dept.getId());
            dto.setDeptName(dept.getName());

            dto.setSpecialty(d.getSpecialty());
            dto.setProfile(d.getProfile());
            dto.setSchedule(d.getSchedule());      // 例如 “周一/周三/周五 上午”

            // ① 今天是不是他的工作日（1~7）
            int today = date.getDayOfWeek().getValue();
            boolean workToday = isWorkDay(d.getWorkDays(), today);

            // ② 号源上限：null / 0 给默认 30
            int quota = (d.getDailyQuota() == null || d.getDailyQuota() <= 0)
                    ? 30
                    : d.getDailyQuota();
            dto.setDailyQuota(quota);

            // ③ 今天已经被占用的号源数（只统计有效状态）
            long used = 0L;
            if (workToday) {
                used = registerRepository.countByDoctorIdAndRegisterTimeBetweenAndStatusIn(
                        d.getId(), start, end, validStatus
                );
            }
            dto.setUsedCount(used);

            // ④ 剩余号源 + 是否已满
            int remaining = Math.max(quota - (int) used, 0);
            dto.setRemaining(remaining);

            boolean full = used >= quota;
            dto.setFull(full);

            // ⑤ 最终：今天可挂号 = 在工作日 && 未满号
            dto.setAvailableToday(workToday && !full);

            return dto;
        }).collect(Collectors.toList());

        // 6. 封装返回
        DeptDoctorOverviewDTO overview = new DeptDoctorOverviewDTO();
        overview.setDept(deptDTO);
        overview.setDoctors(doctorDTOs);
        return overview;
    }


    // ================== 私有工具方法 ==================

    private DeptDetailDTO toDeptDetailDTO(Dept dept) {
        DeptDetailDTO dto = new DeptDetailDTO();
        dto.setId(dept.getId());
        dto.setName(dept.getName());
        dto.setType(dept.getType());
        dto.setFloor(dept.getFloor());
        dto.setDescription(dept.getDescription());
        dto.setLocation(dept.getLocation());
        dto.setOpeningHours(dept.getOpeningHours());
        dto.setSpecialty(dept.getSpecialty());
        return dto;
    }

    /**
     * workDays 例："1,3,5"  表示 周一/周三/周五 坐诊
     * today：1~7 对应 周一~周日
     */
    private boolean isWorkDay(String workDays, int today) {
        if (workDays == null || workDays.isEmpty()) return false;
        String[] arr = workDays.split(",");
        for (String s : arr) {
            if (String.valueOf(today).equals(s.trim())) {
                return true;
            }
        }
        return false;
    }
}
