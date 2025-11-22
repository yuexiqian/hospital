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

    // 排队号（同一天同一医生内递增）
    @Column(name = "queue_no", nullable = false)
    private Integer queueNo;

    // 状态：WAITING / FINISHED / CANCELLED
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // 来源：INTELLIGENT_TRIAGE / MANUAL
    @Column(name = "source", nullable = false, length = 30)
    private String source;

    // 备注
    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

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
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
