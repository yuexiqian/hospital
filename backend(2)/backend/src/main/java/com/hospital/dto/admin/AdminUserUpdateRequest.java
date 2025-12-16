package com.hospital.dto.admin;

import lombok.Data;

@Data
public class AdminUserUpdateRequest {
    private String phone;
    private String role;
    private Integer status; // users.status 1/0

    private String bindType;     // DOCTOR / NURSE / PHARMACIST / NONE
    private Long bindProfileId;  // 绑定档案 id
}
