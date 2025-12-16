package com.hospital.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 费用列表（待缴 + 历史）共用的行 DTO
 */
@Data
public class BillListItemDTO {

    private Long id;

    private Long userId;
    private Long patientId;

    /** 项目名称，如“门诊药品费”、“CT 检查费” */
    private String itemName;

    /** 费用类型：DRUG/EXAM/LAB/REGISTER/OTHER */
    private String category;

    /** 科室 / 医生（可选） */
    private String deptName;
    private String doctorName;

    /** 总金额 */
    private BigDecimal amount;

    /** 状态：UNPAID / PAID */
    private String status;

    /** 费用产生时间 */
    private LocalDateTime createTime;

    /** 支付时间（仅历史列表用） */
    private LocalDateTime paidTime;

    /** 支付方式 */
    private String payMethod;
}
