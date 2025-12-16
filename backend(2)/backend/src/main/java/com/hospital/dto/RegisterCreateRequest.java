package com.hospital.dto;

import lombok.Data;

@Data
public class RegisterCreateRequest {

    private Long patientId;  // 就诊人ID
    private Long deptId;     // 科室ID
    private Long doctorId;   // 医生ID
    private String source;   // INTELLIGENT_TRIAGE / MANUAL
    private String remark;   // 备注（可选）
}
