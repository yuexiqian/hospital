// src/main/java/com/hospital/dto/PharmPrescriptionItemDTO.java
package com.hospital.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 药师端查看的处方明细 DTO
 */
@Data
public class PharmPrescriptionItemDTO {

    private Long id;

    private Long drugId;

    /** 药品名称 */
    private String drugName;

    /** 规格 */
    private String spec;

    /** 剂型（片剂、胶囊等） */
    private String dosageForm;

    /** 单次剂量（文字，比如 “1 片”） */
    private String dosage;

    /** 频次（BID / TID / QD 等） */
    private String frequency;

    /** 天数 */
    private Integer days;

    /** 总数量（片 / 粒 / 支） */
    private Integer quantity;

    /** 单价（来自 drug_inventory.unit_price） */
    private BigDecimal unitPrice;

    /** 本行金额 */
    private BigDecimal amount;

    /** 医生在处方明细上写的备注 */
    private String remark;
}
