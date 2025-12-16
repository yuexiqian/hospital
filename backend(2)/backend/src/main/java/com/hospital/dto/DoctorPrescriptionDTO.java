package com.hospital.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DoctorPrescriptionDTO {

    private Long prescriptionId;
    private String status;          // DRAFT / SUBMITTED / DISPENSED ...
    private BigDecimal totalAmount; // 对应 prescription.total_amount，可先填 0

    private List<DoctorPrescriptionItemDTO> items;
}
