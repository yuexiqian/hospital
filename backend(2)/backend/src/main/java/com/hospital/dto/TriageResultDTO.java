package com.hospital.dto;

import lombok.Data;

import java.util.List;

@Data
public class TriageResultDTO {

    private Long   mainDeptId;
    private String mainDeptName;

    private Long   backupDeptId;
    private String backupDeptName;

    private String reason;   // 推荐理由

    private List<DeptQueueDTO> queueInfo;   // 科室候诊情况
    private List<DoctorSimpleDTO> doctors;  // 推荐医生列表
}
