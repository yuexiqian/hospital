package com.hospital.service.admin.impl;

import com.hospital.dto.admin.*;
import com.hospital.model.*;
import com.hospital.repository.*;
import com.hospital.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final PharmacistRepository pharmacistRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<AdminUserListItemDTO> page(String keyword, String role, Integer status, int page, int size) {
        String kw = keyword == null ? "" : keyword.trim();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 10),
                Sort.by(Sort.Direction.DESC, "userId"));

        Page<User> p;
        if (role != null && !role.isBlank() && status != null) {
            p = userRepository.findByLoginNameContainingAndRoleAndStatus(kw, role, status, pageable);
        } else {
            p = userRepository.findByLoginNameContaining(kw, pageable);
        }

        List<AdminUserListItemDTO> rows = new ArrayList<>();
        for (User u : p.getContent()) rows.add(toDTOWithBind(u));
        return new PageImpl<>(rows, pageable, p.getTotalElements());
    }

    @Override
    @Transactional
    public AdminUserListItemDTO create(AdminUserCreateRequest req) {
        if (req.getLoginName() == null || req.getLoginName().isBlank()) throw new RuntimeException("loginName不能为空");
        if (userRepository.existsByLoginName(req.getLoginName())) throw new RuntimeException("登录名已存在");

        User u = new User();
        u.setLoginName(req.getLoginName());
        u.setPhone(req.getPhone());
        u.setRole(req.getRole() == null ? "PATIENT" : req.getRole());
        u.setStatus(1);

        String rawPwd = (req.getPassword() == null || req.getPassword().isBlank()) ? "123456" : req.getPassword();
        u.setPassword(passwordEncoder.encode(rawPwd));

        u = userRepository.save(u);

        // 创建即绑定
        if (req.getBindType() != null && !req.getBindType().isBlank()
                && req.getBindProfileId() != null
                && !"NONE".equalsIgnoreCase(req.getBindType())) {
            bind(u.getUserId(), req.getBindType(), req.getBindProfileId());

            // 绑定后同步角色（建议）
            if ("DOCTOR".equalsIgnoreCase(req.getBindType())) u.setRole("DOCTOR");
            if ("NURSE".equalsIgnoreCase(req.getBindType())) u.setRole("NURSE");
            if ("PHARMACIST".equalsIgnoreCase(req.getBindType())) u.setRole("PHARMACIST");

            u = userRepository.save(u);
        }

        return toDTOWithBind(u);
    }

    @Override
    @Transactional
    public AdminUserListItemDTO update(Long userId, AdminUserUpdateRequest req) {
        User u = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        if (req.getPhone() != null) u.setPhone(req.getPhone());
        if (req.getRole() != null && !req.getRole().isBlank()) u.setRole(req.getRole());
        if (req.getStatus() != null) u.setStatus(req.getStatus());

        // 绑定/解绑
        if (req.getBindType() != null && !req.getBindType().isBlank()) {
            if ("NONE".equalsIgnoreCase(req.getBindType())) {
                unbindAll(userId);
            } else {
                if (req.getBindProfileId() == null) throw new RuntimeException("bindProfileId不能为空");
                bind(userId, req.getBindType(), req.getBindProfileId());

                // 同步角色
                if ("DOCTOR".equalsIgnoreCase(req.getBindType())) u.setRole("DOCTOR");
                if ("NURSE".equalsIgnoreCase(req.getBindType())) u.setRole("NURSE");
                if ("PHARMACIST".equalsIgnoreCase(req.getBindType())) u.setRole("PHARMACIST");
            }
        }

        u = userRepository.save(u);
        return toDTOWithBind(u);
    }

    @Override @Transactional
    public void enable(Long userId) {
        User u = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        u.setStatus(1);
        userRepository.save(u);
    }

    @Override @Transactional
    public void disable(Long userId) {
        User u = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        u.setStatus(0);
        userRepository.save(u);
    }

    @Override @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User u = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        String raw = (newPassword == null || newPassword.isBlank()) ? "123456" : newPassword;
        u.setPassword(passwordEncoder.encode(raw));
        userRepository.save(u);
    }

    // ---------------- 核心：解绑 + 绑定（三选一） ----------------

    private void unbindAll(Long userId) {
        doctorRepository.findByUserId(userId).ifPresent(d -> { d.setUserId(null); doctorRepository.save(d); });
        nurseRepository.findByUserId(userId).ifPresent(n -> { n.setUserId(null); nurseRepository.save(n); });
        pharmacistRepository.findByUserId(userId).ifPresent(p -> { p.setUserId(null); pharmacistRepository.save(p); });
    }

    private void bind(Long userId, String bindType, Long profileId) {
        // 账号只能绑一种：先清旧绑定
        unbindAll(userId);

        if ("DOCTOR".equalsIgnoreCase(bindType)) {
            Doctor d = doctorRepository.findById(profileId).orElseThrow(() -> new RuntimeException("医生档案不存在"));
            if (d.getUserId() != null && !Objects.equals(d.getUserId(), userId)) {
                throw new RuntimeException("该医生档案已绑定其它账号：" + d.getUserId());
            }
            d.setUserId(userId);
            doctorRepository.save(d);
            return;
        }

        if ("NURSE".equalsIgnoreCase(bindType)) {
            Nurse n = nurseRepository.findById(profileId).orElseThrow(() -> new RuntimeException("护士档案不存在"));
            if (n.getUserId() != null && !Objects.equals(n.getUserId(), userId)) {
                throw new RuntimeException("该护士档案已绑定其它账号：" + n.getUserId());
            }
            n.setUserId(userId);
            nurseRepository.save(n);
            return;
        }

        if ("PHARMACIST".equalsIgnoreCase(bindType)) {
            Pharmacist p = pharmacistRepository.findById(profileId).orElseThrow(() -> new RuntimeException("药师档案不存在"));
            if (p.getUserId() != null && !Objects.equals(p.getUserId(), userId)) {
                throw new RuntimeException("该药师档案已绑定其它账号：" + p.getUserId());
            }
            p.setUserId(userId);
            pharmacistRepository.save(p);
            return;
        }

        throw new RuntimeException("不支持的 bindType：" + bindType);
    }

    private AdminUserListItemDTO toDTOWithBind(User u) {
        AdminUserListItemDTO dto = new AdminUserListItemDTO();
        dto.setUserId(u.getUserId());
        dto.setLoginName(u.getLoginName());
        dto.setPhone(u.getPhone());
        dto.setStatus(u.getStatus());
        dto.setRole(u.getRole());
        dto.setCreateTime(u.getCreateTime());

        doctorRepository.findByUserId(u.getUserId()).ifPresent(d -> {
            dto.setBindType("DOCTOR");
            dto.setBindId(d.getId());
            dto.setBindName(d.getName());
        });
        nurseRepository.findByUserId(u.getUserId()).ifPresent(n -> {
            dto.setBindType("NURSE");
            dto.setBindId(n.getId());
            dto.setBindName(n.getName());
        });
        pharmacistRepository.findByUserId(u.getUserId()).ifPresent(p -> {
            dto.setBindType("PHARMACIST");
            dto.setBindId(p.getId());
            dto.setBindName(p.getName());
        });

        if (dto.getBindType() == null) dto.setBindType("-");
        return dto;
    }
}
