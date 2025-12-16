package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 医生姓名 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 职称 */
    @Column(length = 20)
    private String title;

    /** 所属科室 */
    @Column(name = "dept_id", nullable = false)
    private Long deptId;

    /** 联系电话（可选） */
    @Column(length = 20)
    private String phone;

    /** 状态：1 在职，0 停用 */
    private Integer status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    /* ------------------- 新增字段 ------------------- */

    /** 性别：M / F */
    @Column(length = 1)
    private String gender;

    /** 擅长方向，如：慢性咳嗽、哮喘 */
    @Column(length = 200)
    private String specialty;

    /** 详细简介（TEXT 类型） */
    @Column(columnDefinition = "TEXT")
    private String profile;

    /** 坐诊时间（无固定格式） */
    @Column(length = 100)
    private String schedule;
    // com.hospital.model.Doctor

    @Column(name = "user_id")
    private Long userId;   // ✅ 新增：绑定到 users.user_id，可为空


    /**
     * 坐诊日：
     * 1=周一，2=周二 ... 7=周日
     * 例：1,3,5 表示 周一/周三/周五 坐诊
     */
    @Column(length = 20)
    private String workDays;

    /** 每日可挂号数量 */
    @Column(name = "daily_quota")
    private Integer dailyQuota;
}
