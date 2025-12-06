package com.hospital.controller;

import com.hospital.dto.*;
import com.hospital.model.Dept;
import com.hospital.model.Doctor;
import com.hospital.service.DeptService;
import com.hospital.service.DoctorService;
import com.hospital.service.TriageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class TriageController {

    private final DeptService deptService;
    private final DoctorService doctorService;
    private final TriageService triageService;

    public TriageController(DeptService deptService,
                            DoctorService doctorService,
                            TriageService triageService) {
        this.deptService = deptService;
        this.doctorService = doctorService;
        this.triageService = triageService;
    }

    /**
     * 获取全部科室列表（挂号下拉框用）
     */
    @GetMapping("/depts")
    public AuthResponse<List<Dept>> listDepts() {
        List<Dept> list = deptService.listAll();
        return AuthResponse.success(list);
    }

    /**
     * 根据科室查医生列表（快速挂号下拉框用）
     */
    @GetMapping("/doctors")
    public AuthResponse<List<Doctor>> listDoctors(@RequestParam Long deptId) {
        List<Doctor> list = doctorService.listByDept(deptId);
        return AuthResponse.success(list);
    }

    /**
     * 【新增】科室 + 医生介绍总览
     * 用于首页“分诊两块上面”的科室/医生介绍区域
     *
     * GET /api/depts/{id}/overview?date=2025-11-23
     * date 可不传，默认今天
     */
    @GetMapping("/depts/{id}/overview")
    public AuthResponse<DeptDoctorOverviewDTO> deptOverview(
            @PathVariable("id") Long id,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        DeptDoctorOverviewDTO dto = deptService.getDeptOverviewWithDoctors(id, date);
        return AuthResponse.success(dto);
    }

    /**
     * 智能分诊：根据症状推荐科室+医生
     */
    @PostMapping("/triage/advise")
    public AuthResponse<TriageResultDTO> advise(@RequestBody TriageRequest request) {
        try {
            TriageResultDTO dto = triageService.advise(request);
            return AuthResponse.success(dto);
        } catch (RuntimeException e) {
            return AuthResponse.fail(e.getMessage());
        }
    }
}
