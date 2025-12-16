package com.hospital.controller;

import com.hospital.dto.admin.AdminUserCreateRequest;
import com.hospital.dto.admin.AdminUserListItemDTO;
import com.hospital.dto.admin.AdminUserUpdateRequest;
import com.hospital.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public Page<AdminUserListItemDTO> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminUserService.page(keyword, role, status, page, size);
    }

    @PostMapping
    public AdminUserListItemDTO create(@RequestBody AdminUserCreateRequest req) {
        return adminUserService.create(req);
    }

    @PutMapping("/{userId}")
    public AdminUserListItemDTO update(@PathVariable Long userId, @RequestBody AdminUserUpdateRequest req) {
        return adminUserService.update(userId, req);
    }

    @PostMapping("/{userId}/enable")
    public void enable(@PathVariable Long userId) {
        adminUserService.enable(userId);
    }

    @PostMapping("/{userId}/disable")
    public void disable(@PathVariable Long userId) {
        adminUserService.disable(userId);
    }

    @PostMapping("/{userId}/resetPassword")
    public void resetPassword(@PathVariable Long userId,
                              @RequestParam(required = false) String password) {
        adminUserService.resetPassword(userId, password);
    }
}
