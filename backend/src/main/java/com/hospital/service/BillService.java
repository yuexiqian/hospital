package com.hospital.service;

import com.hospital.dto.BillDetailDTO;
import com.hospital.dto.BillListItemDTO;
import com.hospital.dto.BillsSummaryDTO;

import java.math.BigDecimal;
import java.util.List;

public interface BillService {

    // ====== 你原来就有的（首页 A 区在用） ======
    long countUnpaidByUser(Long userId);

    BigDecimal sumUnpaidAmountByUser(Long userId);

    // ====== 新增：费用中心用 ======

    /** 摘要：待缴金额/笔数 + 历史已缴 */
    BillsSummaryDTO getSummaryByUser(Long userId);

    /** 待缴费用列表 */
    List<BillListItemDTO> listUnpaidByUser(Long userId);

    /** 历史缴费列表 */
    List<BillListItemDTO> listHistoryByUser(Long userId);

    /** 某笔费用 + 明细 */
    BillDetailDTO getBillDetail(Long billId);

    /** 模拟缴费 */
    void payBill(Long billId, String payMethod);
}
