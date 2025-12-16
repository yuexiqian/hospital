// src/main/java/com/hospital/model/DispenseRecord.java
package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "dispense_record")
public class DispenseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long prescriptionId;

    private Long pharmacistId;

    // 新增：药师姓名，对应表里的 pharmacist_name（NOT NULL）
    @Column(name = "pharmacist_name", nullable = false)
    private String pharmacistName;

    private String remark;

    private LocalDateTime dispenseTime;
}
