// src/main/java/com/hospital/dto/PharmPrescriptionSummaryDTO.java
package com.hospital.dto;

import lombok.Data;

@Data
public class PharmPrescriptionSummaryDTO {
    private Long id;              // 处方ID
    private String patientName;   // 患者姓名
    private String patientIdCard; // 可选：证件号
    private String deptName;      // 科室
    private String doctorName;    // 医生
    private String status;        // PENDING / DISPENSED
    private String createTime;    // 字符串形式，前端直接展示
}
