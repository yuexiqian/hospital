package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "prescription")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 对应表字段 register_id
    @Column(name = "register_id", nullable = false)
    private Long registerId;

    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "doctor_id")
    private Long doctorId;

    // ★ 关键：诊断字段，对应 service 里的 getDiag()
    @Column(name = "diag")
    private String diag;

    // ★ 关键：处方总金额，对应 service 里的 getTotalAmount()
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    // PENDING / DISPENSED
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "dispense_time")
    private LocalDateTime dispenseTime;
}
