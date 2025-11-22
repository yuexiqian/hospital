package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long patientId;

    /** 账单类型：REGISTER_FEE / EXAM_FEE / DRUG_FEE / OTHER */
    private String type;

    /** 金额 */
    private BigDecimal amount;

    /** 状态：UNPAID / PAID */
    private String status;

    private LocalDateTime createTime = LocalDateTime.now();

    private LocalDateTime paidTime;
}
