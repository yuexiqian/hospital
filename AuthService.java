package com.hospital.service;

import com.hospital.dto.LoginRequest;
import com.hospital.dto.RegisterRequest;
import com.hospital.dto.UserVO;
import com.hospital.model.User;
import com.hospital.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 患者注册
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
     * 登录（所有角色通用）
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
        vo.setRole(user.getRole());
        return vo;
    }
}
