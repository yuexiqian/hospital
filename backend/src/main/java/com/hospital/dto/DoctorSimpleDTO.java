package com.hospital.dto;

import lombok.Data;

@Data
public class DoctorSimpleDTO {

    private Long   doctorId;
    private String doctorName;
    private String title;

    // 当前该医生候诊人数
    private long   waitCount;
}
