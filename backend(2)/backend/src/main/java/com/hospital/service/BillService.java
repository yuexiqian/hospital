package com.hospital.service;

import com.hospital.dto.BillDetailDTO;
import com.hospital.dto.BillListItemDTO;
import com.hospital.dto.BillsSummaryDTO;
import com.hospital.model.Prescription;
import com.hospital.model.PrescriptionItem;
import com.hospital.model.RegisterRecord;

import java.math.BigDecimal;
import java.util.List;

public interface BillService {

    long countUnpaidByUser(Long userId);

    BigDecimal sumUnpaidAmountByUser(Long userId);

    BillsSummaryDTO getSummaryByUser(Long userId);

    List<BillListItemDTO> listUnpaidByUser(Long userId);

    List<BillListItemDTO> listHistoryByUser(Long userId);

    BillDetailDTO getBillDetail(Long billId);

    void payBill(Long billId, String payMethod);

    // ✅ 提交处方后生成/更新待缴账单
    void upsertPrescriptionBill(RegisterRecord reg,
                                Prescription prescription,
                                List<PrescriptionItem> items);
}
