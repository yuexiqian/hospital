package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "register_record")
public class RegisterRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 登录用户ID（users 表主键）
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 就诊人ID（patients 表主键）
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    // 科室ID
    @Column(name = "dept_id", nullable = false)
    private Long deptId;

    // 医生ID
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    // 展示用：科室名称
    @Column(name = "dept_name", length = 100)
    private String deptName;

    // 展示用：医生名称
    @Column(name = "doctor_name", length = 100)
    private String doctorName;

    // 挂号时间
    @Column(name = "register_time", nullable = false)
    private LocalDateTime registerTime;

    // 排队号（历史字段，为了不为 null 保留）
    @Column(name = "queue_no", nullable = false)
    private Integer queueNo;

    // 挂号状态：WAITING / FINISHED / CANCELLED
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // 来源：INTELLIGENT_TRIAGE / MANUAL
    @Column(name = "source", nullable = false, length = 30)
    private String source;

    // 备注
    @Column(name = "remark")
    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // ===== 分诊相关 =====

    /**
     * 分诊护士账号ID（users.user_id，护士角色的账号）
     */
    @Column(name = "triage_nurse_id")
    private Long triageNurseId;

    @Column(name = "triage_time")
    private LocalDateTime triageTime;

    @Column(name = "triage_note")
    private String triageNote;

    // ===== 候诊队列相关 =====

    /**
     * 队列状态:
     * 0 - 候诊
     * 1 - 已叫号
     * 2 - 就诊中（以后给医生用）
     * 3 - 已完成
     * 4 - 已过号
     * 9 - 已取消
     */
    @Column(name = "queue_status", nullable = false)
    private Integer queueStatus;

    /**
     * 队列优先级，越大越靠前：
     * 普通：0；加急：如 100
     */
    @Column(name = "queue_priority", nullable = false)
    private Integer queuePriority;

    // 最近叫号时间
    @Column(name = "last_call_time")
    private LocalDateTime lastCallTime;

    // 已叫号次数
    @Column(name = "called_times", nullable = false)
    private Integer calledTimes;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (registerTime == null) {
            registerTime = now;
        }
        if (createTime == null) {
            createTime = now;
        }
        updateTime = now;

        if (status == null) {
            status = "WAITING";
        }
        if (source == null) {
            source = "MANUAL";
        }
        if (queueStatus == null) {
            queueStatus = 0; // 默认候诊
        }
        if (queuePriority == null) {
            queuePriority = 0; // 默认不加急
        }
        if (calledTimes == null) {
            calledTimes = 0;
        }
        if (queueNo == null) {
            queueNo = 0; // 防止 null
        }
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
