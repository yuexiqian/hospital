package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 药品库存表，对应 drug_inventory
 */
@Data
@Entity
@Table(name = "drug_inventory")
public class DrugInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 对应 drug_info.id
    @Column(name = "drug_id", nullable = false)
    private Long drugId;

    // 当前库存量（最小单位，如片/粒/支）
    @Column(name = "stock_qty", nullable = false)
    private Integer stockQty;

    // 零售单价
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    // 批号
    @Column(name = "batch_no")
    private String batchNo;

    // 有效期
    @Column(name = "expire_date")
    private LocalDate expireDate;

    // 状态：ACTIVE / STOPPED
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createTime == null) {
            createTime = now;
        }
        updateTime = now;

        if (status == null) {
            status = "ACTIVE";
        }
        if (stockQty == null) {
            stockQty = 0;
        }
        if (unitPrice == null) {
            unitPrice = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
