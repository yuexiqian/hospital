package com.hospital.service.impl;

import com.hospital.dto.*;
import com.hospital.model.*;
import com.hospital.repository.*;
import com.hospital.service.BillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final BillDetailRepository billDetailRepository;
    private final DrugInfoRepository drugInfoRepository;

    public BillServiceImpl(BillRepository billRepository,
                           BillDetailRepository billDetailRepository,
                           DrugInfoRepository drugInfoRepository) {
        this.billRepository = billRepository;
        this.billDetailRepository = billDetailRepository;
        this.drugInfoRepository = drugInfoRepository;
    }

    @Override
    public long countUnpaidByUser(Long userId) {
        return billRepository.countByUserIdAndStatus(userId, "UNPAID");
    }

    @Override
    public BigDecimal sumUnpaidAmountByUser(Long userId) {
        BigDecimal x = billRepository.sumAmountByUserIdAndStatus(userId, "UNPAID");
        return x == null ? BigDecimal.ZERO : x;
    }

    @Override
    public BillsSummaryDTO getSummaryByUser(Long userId) {
        BillsSummaryDTO dto = new BillsSummaryDTO();

        long unpaidCount = billRepository.countByUserIdAndStatus(userId, "UNPAID");
        BigDecimal unpaidAmount = billRepository.sumAmountByUserIdAndStatus(userId, "UNPAID");
        BigDecimal paidTotal = billRepository.sumAmountByUserIdAndStatus(userId, "PAID");

        dto.setUnpaidCount(unpaidCount);
        dto.setUnpaidAmount(unpaidAmount == null ? BigDecimal.ZERO : unpaidAmount);
        dto.setPaidTotal(paidTotal == null ? BigDecimal.ZERO : paidTotal);
        return dto;
    }

    @Override
    public List<BillListItemDTO> listUnpaidByUser(Long userId) {
        List<Bill> list = billRepository.findByUserIdAndStatusOrderByCreateTimeDesc(userId, "UNPAID");
        return list.stream().map(this::toListItemDTO).collect(Collectors.toList());
    }

    @Override
    public List<BillListItemDTO> listHistoryByUser(Long userId) {
        List<Bill> list = billRepository.findByUserIdAndStatusOrderByPaidTimeDesc(userId, "PAID");
        return list.stream().map(this::toListItemDTO).collect(Collectors.toList());
    }

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
                .filter(x -> x != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setTotalAmount(totalAmount);
        return dto;
    }

    @Transactional
    @Override
    public void payBill(Long billId, String payMethod) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("账单不存在"));

        if ("PAID".equalsIgnoreCase(bill.getStatus())) return;

        bill.setStatus("PAID");
        bill.setPayMethod(payMethod);
        bill.setPaidTime(LocalDateTime.now());
        billRepository.save(bill);
    }

    // ✅ 提交处方 => 生成/更新待缴账单 + 明细
    @Transactional
    @Override
    public void upsertPrescriptionBill(RegisterRecord reg,
                                       Prescription prescription,
                                       List<PrescriptionItem> items) {

        if (reg == null || prescription == null) return;
        if (reg.getUserId() == null) throw new RuntimeException("生成账单失败：userId 为空");

        if (items == null || items.isEmpty()) {
            // 没有处方明细就不生成账单
            return;
        }

        Long userId = reg.getUserId();
        Long patientId = reg.getPatientId();

        String itemName = "处方缴费（处方ID:" + prescription.getId() + "）";
        String category = "PRESCRIPTION";

        // ✅ 只 upsert 未缴账单；如果已缴费，就不再生成新 UNPAID（避免重复）
        Bill bill = billRepository
                .findFirstByUserIdAndStatusAndItemName(userId, "UNPAID", itemName)
                .orElse(null);

        if (bill == null) {
            bill = new Bill();
            bill.setUserId(userId);
            bill.setPatientId(patientId);
            bill.setItemName(itemName);
            bill.setCategory(category);
            bill.setDeptName(reg.getDeptName());
            bill.setDoctorName(reg.getDoctorName());
            bill.setStatus("UNPAID");
            bill.setCreateTime(LocalDateTime.now());
        } else {
            // 更新：先清理旧明细
            billDetailRepository.deleteByBillId(bill.getId());
        }

        // ✅ 总金额：以 items 汇总为准（比 prescription.totalAmount 更稳）
        BigDecimal total = items.stream()
                .map(it -> it.getAmount() == null ? BigDecimal.ZERO : it.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        bill.setAmount(total);
        bill = billRepository.save(bill);

        // 写 bill_detail
        for (PrescriptionItem it : items) {
            if (it.getDrugId() == null) continue;

            DrugInfo di = drugInfoRepository.findById(it.getDrugId()).orElse(null);

            String drugName = (di != null && di.getDrugName() != null)
                    ? di.getDrugName()
                    : ("药品ID=" + it.getDrugId());

            String spec = (di != null) ? di.getSpec() : null;

            BigDecimal subtotal = it.getAmount() == null ? BigDecimal.ZERO : it.getAmount();
            Integer qty = it.getQuantity() == null ? 1 : it.getQuantity();

            BigDecimal unitPrice;
            if (qty <= 0) {
                unitPrice = subtotal;
            } else {
                unitPrice = subtotal.divide(BigDecimal.valueOf(qty), 2, RoundingMode.HALF_UP);
            }

            BillDetail bd = new BillDetail();
            bd.setBillId(bill.getId());
            bd.setItemType("DRUG");
            bd.setItemName(drugName);
            bd.setSpec(spec);
            bd.setUnitPrice(unitPrice);
            bd.setQuantity(qty);
            bd.setUnit("盒");
            bd.setSubtotalAmount(subtotal);

            billDetailRepository.save(bd);
        }
    }

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
