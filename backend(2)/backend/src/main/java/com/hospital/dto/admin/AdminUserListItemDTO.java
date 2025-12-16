package com.hospital.dto.admin;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserListItemDTO {
    private Long userId;
    private String loginName;
    private String phone;
    private Integer status;          // 1启用 0停用
    private String role;             // ADMIN/DOCTOR/NURSE/PHARMACIST/PATIENT
    private LocalDateTime createTime;

    private String bindType;         // DOCTOR/NURSE/PHARMACIST 或 "-"
    private Long bindId;             // 对应医生/护士/药师表的id
    private String bindName;         // 对应档案姓名
}
