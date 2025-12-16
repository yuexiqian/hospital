package com.hospital.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterInfoDTO {

    private Long id;

    private Long userId;
    private Long patientId;
    private String patientName; // 需要这个字段

    private Long deptId;
    private Long doctorId;

    private String deptName;
    private String doctorName;

    private Integer queueNo;

    private String status;
    private String source;
    private String remark;

    private LocalDateTime registerTime;

    /** A 区队列卡片用：前方人数（运行时算的，不一定存表） */
    private Integer aheadCount;

    /** A 区队列卡片用：预计等待分钟数（运行时算的） */
    private Integer estimateWaitMin;
}
