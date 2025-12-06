package com.hospital.dto;

import lombok.Data;

/**
 * 首页科室介绍区域中，医生介绍 + 当日号源情况
 */
@Data
public class DoctorSlotDTO {

    private Long id;
    private String name;
    private String gender;
    private String title;

    private Long deptId;
    private String deptName;

    // 医生介绍相关
    private String specialty;   // 擅长方向
    private String schedule;    // 坐诊时间，如：周一/周三/周五 上午
    private String profile;     // 详细简介（可选，前端可做“展开更多”）

    // 当天号源情况
    private boolean availableToday; // 今天是否坐诊
    private boolean full;           // 今日号源是否已满
    private Integer dailyQuota;     // 每日最大号源（配置在 doctor.daily_quota）
    private Long usedCount;         // 今日已挂号数量
    private Integer remaining;      // 今日剩余号源数
}
