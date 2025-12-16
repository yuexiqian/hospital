package com.hospital.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BillDetailDTO {

    private Long billId;

    /** 项目名称 + 类型 */
    private String itemName;
    private String category;

    /** 总金额 */
    private BigDecimal amount;

    /** 状态/时间/方式 */
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime paidTime;
    private String payMethod;

    /** 明细列表 */
    private List<BillDetailItemDTO> items;

    /** 汇总信息：明细条数 + 合计金额（一般等于 amount） */
    private Integer totalItems;
    private BigDecimal totalAmount;
}
