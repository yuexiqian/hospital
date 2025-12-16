package com.hospital.dto;

import lombok.Data;

@Data
public class DeptQueueDTO {

    private Long   deptId;
    private String deptName;

    // 当前候诊人数
    private long   waitCount;

    // 预计等待时间（分钟）
    private long   estimateWaitMin;
}
