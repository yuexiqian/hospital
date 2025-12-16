package com.hospital.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId;

    @Column(nullable = false, length = 50)
    private String name;        // 就诊人姓名

    @Column(name = "id_type", nullable = false, length = 20)
    private String idType;      // 证件类型：身份证/护照/医保卡

    @Column(name = "id_card", nullable = false, length = 50)
    private String idCard;      // 证件号码

    @Column(name = "user_id", nullable = false)
    private Long userId;        // 关联用户ID（users.user_id）

    @Column(length = 20)
    private String phone;       // 联系电话

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    // ====== getter / setter ======

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
