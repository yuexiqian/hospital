package com.hospital.dto.admin;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminRegisterListItemDTO {
    private Long id;
    private Long userId;
    private Long patientId;

    private Long deptId;
    private String deptName;

    private Long doctorId;
    private String doctorName;

    private LocalDateTime registerTime;
    private Integer queueNo;

    // ✅ 改这里：和 RegisterRecord.status 对齐
    private String status;

    private Integer queueStatus;
    private Integer queuePriority;

    private Long triageNurseId;
    private LocalDateTime triageTime;
    private String triageNote;

    private LocalDateTime lastCallTime;
    private Integer calledTimes;
}
