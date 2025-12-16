package com.hospital.controller;

import com.hospital.dto.AuthResponse;
import com.hospital.dto.RegisterCreateRequest;
import com.hospital.dto.RegisterInfoDTO;
import com.hospital.dto.TodayRegisterListDTO;
import com.hospital.service.RegisterService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registers")
@CrossOrigin
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    // ===================== 1. 创建挂号 =====================
    /**
     * POST /api/registers?userId=1
     * Body:
     * {
     *   "patientId": 3,
     *   "deptId": 2,
     *   "doctorId": 10,
     *   "source": "MANUAL_PATIENT" 或 "INTELLIGENT_TRIAGE",
     *   "remark": "可选备注"
     * }
     */
    @PostMapping
    public AuthResponse<RegisterInfoDTO> createRegister(
            @RequestParam("userId") Long userId,
            @RequestBody RegisterCreateRequest req
    ) {
        try {
            RegisterInfoDTO dto = registerService.createRegister(userId, req);
            return AuthResponse.success(dto);
        } catch (RuntimeException e) {
            return AuthResponse.fail(e.getMessage());
        }
    }

    // ===================== 2. 今日挂号列表 =====================
    /**
     * GET /api/registers/today?userId=1
     */
    @GetMapping("/today")
    public AuthResponse<TodayRegisterListDTO> getTodayRegisters(
            @RequestParam("userId") Long userId
    ) {
        TodayRegisterListDTO dto = registerService.getTodayRegisters(userId);
        return AuthResponse.success(dto);
    }
}
