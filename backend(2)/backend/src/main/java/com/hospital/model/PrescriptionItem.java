package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "prescription_item")
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prescription_id", nullable = false)
    private Long prescriptionId;

    @Column(name = "drug_id", nullable = false)
    private Long drugId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "dosage")
    private String dosage;

    @Column(name = "frequency")
    private String frequency;

    @Column(name = "days")
    private Integer days;

    @Column(name = "remark")
    private String remark;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;
}
