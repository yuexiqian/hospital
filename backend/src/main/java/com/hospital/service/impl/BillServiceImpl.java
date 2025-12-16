package com.hospital.service.impl;

import com.hospital.dto.BillDetailDTO;
import com.hospital.dto.BillDetailItemDTO;
import com.hospital.dto.BillListItemDTO;
import com.hospital.dto.BillsSummaryDTO;
import com.hospital.model.Bill;
import com.hospital.model.BillDetail;
import com.hospital.repository.BillDetailRepository;
import com.hospital.repository.BillRepository;
import com.hospital.service.BillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final BillDetailRepository billDetailRepository;

    public BillServiceImpl(BillRepository billRepository,
                           BillDetailRepository billDetailRepository) {
        this.billRepository = billRepository;
        this.billDetailRepository = billDetailRepository;
    }

    // ================= 原来就有的两个方法 =================

    @Override
    public long countUnpaidByUser(Long userId) {
        return billRepository.countByUserIdAndStatus(userId, "UNPAID");
    }

    @Override
    public BigDecimal sumUnpaidAmountByUser(Long userId) {
        return billRepository.sumAmountByUserIdAndStatus(userId, "UNPAID");
    }

    // ================= 新增：摘要 =================

    @Override
    public BillsSummaryDTO getSummaryByUser(Long userId) {
        BillsSummaryDTO dto = new BillsSummaryDTO();

        long unpaidCount = billRepository.countByUserIdAndStatus(userId, "UNPAID");
        BigDecimal unpaidAmount = billRepository.sumAmountByUserIdAndStatus(userId, "UNPAID");
        BigDecimal paidTotal = billRepository.sumAmountByUserIdAndStatus(userId, "PAID");

        if (unpaidAmount == null) unpaidAmount = BigDecimal.ZERO;
        if (paidTotal == null) paidTotal = BigDecimal.ZERO;

        dto.setUnpaidCount(unpaidCount);
        dto.setUnpaidAmount(unpaidAmount);
        dto.setPaidTotal(paidTotal);

        return dto;
    }

    // ================= 新增：待缴列表 =================

    @Override
    public List<BillListItemDTO> listUnpaidByUser(Long userId) {
        List<Bill> list = billRepository
                .findByUserIdAndStatusOrderByCreateTimeDesc(userId, "UNPAID");
        return list.stream().map(this::toListItemDTO).collect(Collectors.toList());
    }

    // ================= 新增：历史列表 =================

    @Override
    public List<BillListItemDTO> listHistoryByUser(Long userId) {
        List<Bill> list = billRepository
                .findByUserIdAndStatusOrderByPaidTimeDesc(userId, "PAID");
        return list.stream().map(this::toListItemDTO).collect(Collectors.toList());
    }

    // ================= 新增：单笔 + 明细 =================

    @Override
    public BillDetailDTO getBillDetail(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("账单不存在"));

        List<BillDetail> details = billDetailRepository.findByBillId(billId);

        BillDetailDTO dto = new BillDetailDTO();
        dto.setBillId(bill.getId());
        dto.setItemName(bill.getItemName());
        dto.setCategory(bill.getCategory());
        dto.setAmount(bill.getAmount());
        dto.setStatus(bill.getStatus());
        dto.setCreateTime(bill.getCreateTime());
        dto.setPaidTime(bill.getPaidTime());
        dto.setPayMethod(bill.getPayMethod());

        List<BillDetailItemDTO> items = details.stream().map(d -> {
            BillDetailItemDTO x = new BillDetailItemDTO();
            x.setId(d.getId());
            x.setItemType(d.getItemType());
            x.setItemName(d.getItemName());
            x.setSpec(d.getSpec());
            x.setUnitPrice(d.getUnitPrice());
            x.setQuantity(d.getQuantity());
            x.setUnit(d.getUnit());
            x.setSubtotalAmount(d.getSubtotalAmount());
            return x;
        }).collect(Collectors.toList());

        dto.setItems(items);
        dto.setTotalItems(items.size());
        BigDecimal totalAmount = items.stream()
                .map(BillDetailItemDTO::getSubtotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAmount(totalAmount);

        return dto;
    }

    // ================= 新增：缴费 =================

    @Transactional
    @Override
    public void payBill(Long billId, String payMethod) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("账单不存在"));

        if ("PAID".equalsIgnoreCase(bill.getStatus())) {
            return; // 已缴费就不重复处理
        }

        bill.setStatus("PAID");
        bill.setPayMethod(payMethod);
        bill.setPaidTime(LocalDateTime.now());

        billRepository.save(bill);
    }

    // ================= 工具方法：Bill -> DTO =================

    private BillListItemDTO toListItemDTO(Bill bill) {
        BillListItemDTO dto = new BillListItemDTO();
        dto.setId(bill.getId());
        dto.setUserId(bill.getUserId());
        dto.setPatientId(bill.getPatientId());
        dto.setItemName(bill.getItemName());
        dto.setCategory(bill.getCategory());
        dto.setDeptName(bill.getDeptName());
        dto.setDoctorName(bill.getDoctorName());
        dto.setAmount(bill.getAmount());
        dto.setStatus(bill.getStatus());
        dto.setCreateTime(bill.getCreateTime());
        dto.setPaidTime(bill.getPaidTime());
        dto.setPayMethod(bill.getPayMethod());
        return dto;
    }
}
