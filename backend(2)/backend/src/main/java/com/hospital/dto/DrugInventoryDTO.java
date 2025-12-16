// src/main/java/com/hospital/dto/DrugInventoryDTO.java
package com.hospital.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 药品库存列表 DTO
 */
@Data
public class DrugInventoryDTO {

    private Long id;

    // 对应 drug_info.id
    private Long drugId;

    // 为了列表展示方便，冗余一些药品信息
    private String drugName;      // 药品名
    private String genericName;   // 通用名
    private String spec;          // 规格
    private String dosageForm;
    private String Category; // 剂型

    // 当前库存量（最小单位）
    private Integer stockQty;

    // 零售单价
    private BigDecimal unitPrice;

    // 批号
    private String batchNo;

    // 有效期
    private LocalDate expireDate;

    // 状态：ACTIVE / STOPPED
    private String status;

    // 创建 / 更新时间（InventoryServiceImpl 里会用到）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
