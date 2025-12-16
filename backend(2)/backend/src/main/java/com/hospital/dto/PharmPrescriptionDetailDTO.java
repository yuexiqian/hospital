// src/main/java/com/hospital/dto/PharmPrescriptionDetailDTO.java
package com.hospital.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PharmPrescriptionDetailDTO {

    private PharmPrescriptionSummaryDTO header;

    private List<PharmPrescriptionItemDTO> items;

    private BigDecimal totalAmount;  // 总金额
    private String diag;             // 诊断（可选）
}
