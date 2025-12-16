// src/main/java/com/hospital/service/AuthService.java
package com.hospital.service;

import com.hospital.dto.LoginRequest;
import com.hospital.dto.RegisterRequest;
import com.hospital.dto.UserVO;
import com.hospital.model.User;
import com.hospital.model.Doctor;
import com.hospital.repository.UserRepository;
import com.hospital.repository.DoctorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // ✨ 新增：医生表的仓库
    private final DoctorRepository doctorRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.doctorRepository = doctorRepository;
    }

    /**
     * 患者注册
     * 护士 / 医生 / 药师 / 管理员账号不开放注册，一般由管理员在后台创建
     */
    public void registerPatient(RegisterRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        Optional<User> exist = userRepository.findByLoginName(req.getLoginName());
        if (exist.isPresent()) {
            throw new RuntimeException("该账号已存在");
        }

        User user = new User();
        user.setLoginName(req.getLoginName()); // 一般就是手机号
        user.setPhone(req.getPhone());
        user.setRole("PATIENT");  // 患者固定角色
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setStatus(1);

        userRepository.save(user);
    }

    /**
     * 登录（所有角色通用：患者 / 护士 / 医生 / 药师 / 管理员）
     *
     * 关键点：
     *  - loginName：患者=手机号；护士/医生=工号；药师/管理员=自己设置的账号
     *  - 根据 users.role 返回不同角色，前端拿到 role 决定跳转哪个首页
     */
    public UserVO login(LoginRequest req) {
        User user = userRepository.findByLoginName(req.getLoginName())
                .orElseThrow(() -> new RuntimeException("账号不存在"));

        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        UserVO vo = new UserVO();
        vo.setUserId(user.getUserId());
        vo.setLoginName(user.getLoginName());
        vo.setPhone(user.getPhone());
        // ⭐⭐ 前端就靠这个区分 PATIENT / NURSE / DOCTOR / ...
        vo.setRole(user.getRole());

        // === 仅给医生加 doctorId + name，其它角色不动 ===
        if ("DOCTOR".equals(user.getRole())) {
            // 这里按 phone 去 doctor 表里找刚才插的那条医生记录
            Doctor doctor = doctorRepository.findByPhone(user.getPhone())
                    .orElse(null);
            if (doctor != null) {
                vo.setDoctorId(doctor.getId());
                vo.setName(doctor.getName());
            }
        }

        return vo;
    }

    // 如果以后想让管理员创建护士/医生账号，可以加类似：
    // public void createStaff(String loginName, String password, String role) { ... }
}
