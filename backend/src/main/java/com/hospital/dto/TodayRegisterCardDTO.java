package com.hospital.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodayRegisterCardDTO {

    /** 今天是否有挂号记录 */
    private boolean hasRegister;

    private Long registerId;
    private String deptName;
    private String doctorName;
    private Integer queueNo;
    private String status;
    private LocalDateTime registerTime;
}
