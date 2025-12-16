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

    /** 用户ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 就诊人ID */
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    /** 费用项目名称，如门诊药品费、CT检查费 */
    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName;

    /** 费用类型：DRUG/EXAM/LAB/REGISTER/OTHER */
    @Column(name = "category", nullable = false, length = 20)
    private String category;

    /** 科室名称 */
    @Column(name = "dept_name", length = 255)
    private String deptName;

    /** 医生名称 */
    @Column(name = "doctor_name", length = 255)
    private String doctorName;

    /** 费用总金额 */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /** 状态：UNPAID/PAID */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /** 支付方式：SELF_SERVICE/CASH/WECHAT/ALIPAY 等 */
    @Column(name = "pay_method", length = 30)
    private String payMethod;

    /** 费用产生时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /** 费用支付时间 */
    @Column(name = "paid_time")
    private LocalDateTime paidTime;
}
