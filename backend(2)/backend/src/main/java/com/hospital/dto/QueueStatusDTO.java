package com.hospital.dto;

import lombok.Data;

@Data
public class QueueStatusDTO {
    private boolean hasWaiting;
    private String deptName;
    private String doctorName;
    private Integer aheadCount;
    private Integer estimateWaitMin;

    // 新增字段：就诊人信息
    private Long patientId;
    private String patientName;
}

