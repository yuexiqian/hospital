package com.hospital.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DoctorPrescriptionItemDTO {

    private Long itemId;        // prescription_item.id
    private Long drugId;        // prescription_item.drug_id

    private Integer quantity;   // quantity
    private String dosage;      // dosage
    private String frequency;   // frequency
    private Integer days;       // days
    private String remark;      // remark

    private BigDecimal amount;  // amount（可以先 0，真正金额由别的逻辑算）

    // 仅供前端显示（来自 drug_info），不落表：
    private String drugName;
    private String spec;
}
