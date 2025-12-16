// src/main/java/com/hospital/service/impl/PharmPrescriptionServiceImpl.java
package com.hospital.service.impl;

import com.hospital.dto.DispenseRequest;
import com.hospital.dto.PharmPrescriptionDetailDTO;
import com.hospital.dto.PharmPrescriptionItemDTO;
import com.hospital.dto.PharmPrescriptionSummaryDTO;
import com.hospital.model.*;
import com.hospital.repository.*;
import com.hospital.service.PharmPrescriptionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PharmPrescriptionServiceImpl implements PharmPrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final DrugInfoRepository drugInfoRepository;
    private final DrugInventoryRepository drugInventoryRepository;
    private final DispenseRecordRepository dispenseRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DeptRepository deptRepository;

    public PharmPrescriptionServiceImpl(PrescriptionRepository prescriptionRepository,
                                        PrescriptionItemRepository prescriptionItemRepository,
                                        DrugInfoRepository drugInfoRepository,
                                        DrugInventoryRepository drugInventoryRepository,
                                        DispenseRecordRepository dispenseRecordRepository,
                                        PatientRepository patientRepository,
                                        DoctorRepository doctorRepository,
                                        DeptRepository deptRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.drugInfoRepository = drugInfoRepository;
        this.drugInventoryRepository = drugInventoryRepository;
        this.dispenseRecordRepository = dispenseRecordRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.deptRepository = deptRepository;
    }

    // ================== 处方列表 ==================
    @Override
    public List<PharmPrescriptionSummaryDTO> listPrescriptions(LocalDate date, String status) {
        if (date == null) {
            date = LocalDate.now();
        }
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Prescription> list;
        if (status == null || status.isBlank()) {
            list = prescriptionRepository.findByCreateTimeBetween(start, end);
        } else {
            list = prescriptionRepository.findByCreateTimeBetweenAndStatus(start, end, status);
        }

        return list.stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    // ================== 处方详情 ==================
    @Override
    public PharmPrescriptionDetailDTO getPrescriptionDetail(Long id) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("处方不存在，ID=" + id));

        List<PrescriptionItem> items = prescriptionItemRepository.findByPrescriptionId(id);

        PharmPrescriptionDetailDTO dto = new PharmPrescriptionDetailDTO();
        dto.setHeader(toSummaryDto(p));

        List<PharmPrescriptionItemDTO> itemDtos = items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);

        BigDecimal total = items.stream()
                .map(i -> i.getAmount() == null ? BigDecimal.ZERO : i.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAmount(total);

        // 如果实体里面有 diag 字段，就用它
        dto.setDiag(p.getDiag());

        return dto;
    }

    // ================== 发药 ==================
    @Override
    public void dispense(Long id, DispenseRequest req) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("处方不存在，ID=" + id));

        if ("DISPENSED".equalsIgnoreCase(p.getStatus())) {
            throw new RuntimeException("该处方已发药，无需重复操作");
        }

        List<PrescriptionItem> items = prescriptionItemRepository.findByPrescriptionId(id);

        // 1) 先检查库存是否足够
        for (PrescriptionItem item : items) {
            Long drugId = item.getDrugId();
            int needQty = item.getQuantity() == null ? 0 : item.getQuantity();

            DrugInventory inv = drugInventoryRepository
                    .findFirstByDrugIdOrderByIdAsc(drugId)
                    .orElseThrow(() -> new RuntimeException("药品ID=" + drugId + " 尚未建库存记录"));

            if (inv.getStockQty() < needQty) {
                // 为了报错信息好看一点，从 drug_info 表里拿药品名
                String drugName = drugInfoRepository.findById(drugId)
                        .map(DrugInfo::getDrugName)
                        .orElse("ID=" + drugId);
                throw new RuntimeException("药品【" + drugName
                        + "】库存不足，需要 " + needQty + "，当前 " + inv.getStockQty());
            }
        }

        // 2) 扣减库存
        for (PrescriptionItem item : items) {
            Long drugId = item.getDrugId();
            int needQty = item.getQuantity() == null ? 0 : item.getQuantity();

            DrugInventory inv = drugInventoryRepository
                    .findFirstByDrugIdOrderByIdAsc(drugId)
                    .orElseThrow(() -> new RuntimeException("药品ID=" + drugId + " 尚未建库存记录"));

            inv.setStockQty(inv.getStockQty() - needQty);
            drugInventoryRepository.save(inv);
        }

        // 3) 更新处方状态
        p.setStatus("DISPENSED");
        p.setDispenseTime(LocalDateTime.now());
        prescriptionRepository.save(p);

// 4) 记录发药日志
        DispenseRecord record = new DispenseRecord();
        record.setPrescriptionId(p.getId());
        record.setPharmacistId(req.getPharmacistId());

// 从请求里拿药师姓名，避免 pharmacist_name 为空
        String pharmacistName = req.getPharmacistName();
        if (pharmacistName == null || pharmacistName.isBlank()) {
            // 给一个兜底值，防止再次因为 NOT NULL 报错
            pharmacistName = "系统药师";
        }
        record.setPharmacistName(pharmacistName);

        record.setRemark(req.getRemark());
        record.setDispenseTime(LocalDateTime.now());
        dispenseRecordRepository.save(record);

    }

    // ================== 私有组装方法 ==================

    private PharmPrescriptionSummaryDTO toSummaryDto(Prescription p) {
        PharmPrescriptionSummaryDTO dto = new PharmPrescriptionSummaryDTO();
        dto.setId(p.getId());
        dto.setStatus(p.getStatus());
        dto.setCreateTime(p.getCreateTime() == null ? "" : p.getCreateTime().toString());

        // 患者
        if (p.getPatientId() != null) {
            patientRepository.findById(p.getPatientId()).ifPresent(patient -> {
                dto.setPatientName(patient.getName());
                dto.setPatientIdCard(patient.getIdCard());
            });
        }

        // 医生 + 科室
        if (p.getDoctorId() != null) {
            doctorRepository.findById(p.getDoctorId()).ifPresent(doc -> {
                dto.setDoctorName(doc.getName());
                if (doc.getDeptId() != null) {
                    deptRepository.findById(doc.getDeptId()).ifPresent(dept -> {
                        // 这里用 Dept 实体里真实存在的字段名
                        dto.setDeptName(dept.getName());
                    });
                }
            });
        }

        return dto;
    }

    private PharmPrescriptionItemDTO toItemDto(PrescriptionItem item) {
        PharmPrescriptionItemDTO dto = new PharmPrescriptionItemDTO();
        dto.setId(item.getId());
        dto.setDrugId(item.getDrugId());

        if (item.getDrugId() != null) {
            Long drugId = item.getDrugId();

            // 1) 基本药品信息来自 drug_info
            drugInfoRepository.findById(drugId).ifPresent(d -> {
                dto.setDrugName(d.getDrugName());
                dto.setSpec(d.getSpec());
                dto.setDosageForm(d.getDosageForm());
            });

            // 2) 单价来自 drug_inventory（这里取最早的一条库存记录）
            drugInventoryRepository.findFirstByDrugIdOrderByIdAsc(drugId)
                    .ifPresent(inv -> dto.setUnitPrice(inv.getUnitPrice()));
        }

        dto.setDosage(item.getDosage());
        dto.setFrequency(item.getFrequency());
        dto.setDays(item.getDays());
        dto.setQuantity(item.getQuantity());
        dto.setAmount(item.getAmount());
        dto.setRemark(item.getRemark());
        return dto;
    }
}
