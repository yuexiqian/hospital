package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bill_detail")
public class BillDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属费用ID，对应 bill.id */
    @Column(name = "bill_id", nullable = false)
    private Long billId;

    /** 明细类型：DRUG/EXAM/LAB/OTHER */
    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType;

    /** 药品名或项目名 */
    @Column(name = "item_name", nullable = false, length = 255)
    private String itemName;

    /** 规格 */
    @Column(name = "spec", length = 255)
    private String spec;

    /** 单价 */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /** 数量 */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /** 单位：盒/瓶/次/项 */
    @Column(name = "unit", length = 20)
    private String unit;

    /** 金额小计 */
    @Column(name = "subtotal_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalAmount;

    /** 创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;
}
