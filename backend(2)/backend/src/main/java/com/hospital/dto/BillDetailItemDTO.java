package com.hospital.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillDetailItemDTO {

    private Long id;

    /** DRUG/EXAM/LAB/OTHER */
    private String itemType;

    /** 药品名 / 项目名 */
    private String itemName;

    /** 规格 */
    private String spec;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 数量 */
    private Integer quantity;

    /** 单位：盒/瓶/次/项 */
    private String unit;

    /** 金额小计 */
    private BigDecimal subtotalAmount;
}
