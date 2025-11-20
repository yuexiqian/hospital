package com.hospital.controller;

import com.hospital.dto.*;
import com.hospital.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin   // 如果前端单独起项目，用这个允许跨域
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 患者注册
     */
    @PostMapping("/register")
    public AuthResponse<Void> register(@RequestBody RegisterRequest req) {
        try {
            authService.registerPatient(req);
            return AuthResponse.success(null);
        } catch (RuntimeException e) {
            return AuthResponse.fail(e.getMessage());
        }
    }

    /**
     * 登录（所有角色账号）
     */
    @PostMapping("/login")
    public AuthResponse<UserVO> login(@RequestBody LoginRequest req) {
        try {
            UserVO vo = authService.login(req);
            return AuthResponse.success(vo);
        } catch (RuntimeException e) {
            return AuthResponse.fail(e.getMessage());
        }
    }
}
