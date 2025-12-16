package com.hospital.dto.admin;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DashboardSummaryDTO {
    private String date;          // yyyy-MM-dd
    private long registerTotal;   // 今日挂号总数
    private long triaged;         // 今日已分诊数（triage_time not null）

    private List<QueueStatusCountDTO> byQueueStatus = new ArrayList<>();
    private List<DeptCountDTO> byDept = new ArrayList<>();

    @Data
    public static class QueueStatusCountDTO {
        private Integer queueStatus;
        private Long cnt;
    }

    @Data
    public static class DeptCountDTO {
        private Long deptId;
        private String deptName;
        private Long cnt;
    }
}
