package com.hospital.dto.admin;

import lombok.Data;

@Data
public class AdminUserCreateRequest {
    private String loginName;
    private String password; // 空则默认 123456
    private String phone;
    private String role;     // ADMIN/DOCTOR/NURSE/PHARMACIST/PATIENT（创建时可先随便填）

    // 绑定（核心）
    private String bindType;     // DOCTOR / NURSE / PHARMACIST / NONE(可不传)
    private Long bindProfileId;  // doctor.id / nurse.id / pharmacist.id
}
