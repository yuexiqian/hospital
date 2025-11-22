package com.hospital.controller;

import com.hospital.dto.*;
import com.hospital.model.Dept;
import com.hospital.model.Doctor;
import com.hospital.service.DeptService;
import com.hospital.service.DoctorService;
import com.hospital.service.TriageService;
import org.springframework.web.bind.annotation.*;

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
     * 获取全部科室列表
     */
    @GetMapping("/depts")
    public AuthResponse<List<Dept>> listDepts() {
        List<Dept> list = deptService.listAll();
        return AuthResponse.success(list);
    }

    /**
     * 根据科室查医生列表
     */
    @GetMapping("/doctors")
    public AuthResponse<List<Doctor>> listDoctors(@RequestParam Long deptId) {
        List<Doctor> list = doctorService.listByDept(deptId);
        return AuthResponse.success(list);
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
