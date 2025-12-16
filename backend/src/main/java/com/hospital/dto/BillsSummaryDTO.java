package com.hospital.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillsSummaryDTO {

    /** 待缴费笔数 */
    private long unpaidCount;

    /** 待缴费金额 */
    private BigDecimal unpaidAmount;

    /** 历史已缴费总额 */
    private BigDecimal paidTotal;
}
