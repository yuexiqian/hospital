package com.hospital.dto.admin;

import lombok.Data;

@Data
public class StaffOptionDTO {
    private Long id;
    private String name;
    private Long deptId;
    private Integer statusOrActive; // doctor.status 或 nurse/pharm is_active
    private Long userId;            // 当前是否已绑定账号（null=未绑定）
}
