package com.hospital.service.admin;

import com.hospital.dto.admin.*;
import org.springframework.data.domain.Page;

public interface AdminUserService {
    Page<AdminUserListItemDTO> page(String keyword, String role, Integer status, int page, int size);

    AdminUserListItemDTO create(AdminUserCreateRequest req);
    AdminUserListItemDTO update(Long userId, AdminUserUpdateRequest req);

    void enable(Long userId);
    void disable(Long userId);
    void resetPassword(Long userId, String newPassword);
}
