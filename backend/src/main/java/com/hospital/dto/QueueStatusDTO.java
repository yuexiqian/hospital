package com.hospital.dto;

import lombok.Data;

@Data
public class QueueStatusDTO {
    private boolean hasWaiting;
    private String deptName;
    private String doctorName;
    private Integer aheadCount;
    private Integer estimateWaitMin;
}

