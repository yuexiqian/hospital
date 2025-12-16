package com.hospital.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新增 / 编辑库存记录 请求体
 */
@Data
public class DrugInventorySaveRequest {

    private Long drugId;

    private Integer stockQty;

    private BigDecimal unitPrice;

    private String batchNo;

    private LocalDate expireDate;

    // ACTIVE / STOPPED
    private String status;
}
