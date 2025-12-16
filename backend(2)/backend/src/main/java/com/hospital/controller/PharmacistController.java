package com.hospital.controller;

import com.hospital.dto.AuthResponse;
import com.hospital.dto.DispenseRequest;
import com.hospital.dto.DrugInfoDTO;
import com.hospital.dto.DrugInfoUpdateRequest;
import com.hospital.dto.DrugInventoryDTO;
import com.hospital.dto.InventoryAdjustRequest;
import com.hospital.dto.PharmPrescriptionDetailDTO;
import com.hospital.dto.PharmPrescriptionSummaryDTO;
import com.hospital.service.InventoryService;
import com.hospital.service.PharmacistService;
import com.hospital.service.PharmPrescriptionService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/pharmacist")
@CrossOrigin
public class PharmacistController {

    private final PharmacistService pharmacistService;
    private final InventoryService inventoryService;
    private final PharmPrescriptionService pharmPrescriptionService;

    public PharmacistController(PharmacistService pharmacistService,
                                InventoryService inventoryService,
                                PharmPrescriptionService pharmPrescriptionService) {
        this.pharmacistService = pharmacistService;
        this.inventoryService = inventoryService;
        this.pharmPrescriptionService = pharmPrescriptionService;
    }

    // ================== 一、药品知识库 ==================

    /**
     * 药品知识库列表 / 搜索
     * GET /api/pharmacist/drug-info?keyword=xxx
     */
    @GetMapping("/drug-info")
    public AuthResponse<List<DrugInfoDTO>> listDrugInfos(
            @RequestParam(required = false) String keyword
    ) {
        List<DrugInfoDTO> list = pharmacistService.searchDrugInfos(keyword);
        return AuthResponse.success(list);
    }

    /**
     * 某个药品知识详情
     * GET /api/pharmacist/drug-info/{id}
     */
    @GetMapping("/drug-info/{id}")
    public AuthResponse<DrugInfoDTO> getDrugInfo(@PathVariable("id") Long id) {
        DrugInfoDTO dto = pharmacistService.getDrugInfo(id);
        return AuthResponse.success(dto);
    }

    /**
     * 更新某个药品知识
     * PUT /api/pharmacist/drug-info/{id}
     */
    @PutMapping("/drug-info/{id}")
    public AuthResponse<Void> updateDrugInfo(
            @PathVariable Long id,
            @RequestBody DrugInfoUpdateRequest req
    ) {
        pharmacistService.updateDrugInfo(id, req);
        return AuthResponse.success(null);
    }

    /**
     * 新建药品知识
     * POST /api/pharmacist/drug-info
     */
    @PostMapping("/drug-info")
    public AuthResponse<DrugInfoDTO> createDrugInfo(
            @RequestBody DrugInfoUpdateRequest req
    ) {
        DrugInfoDTO dto = pharmacistService.createDrugInfo(req);
        return AuthResponse.success(dto);
    }

    // ================== 二、药品库存 ==================

    /**
     * 库存列表
     * GET /api/pharmacist/inventory
     *
     * 注意：这里使用的是 inventoryService.listInventories()
     *      没有任何参数，也没有叫 listInventory(String) 的方法。
     */
    @GetMapping("/inventory")
    public AuthResponse<List<DrugInventoryDTO>> listInventories() {
        List<DrugInventoryDTO> list = inventoryService.listInventories();
        return AuthResponse.success(list);
    }

    /**
     * 查看单条库存
     * GET /api/pharmacist/inventory/{id}
     */
    @GetMapping("/inventory/{id}")
    public AuthResponse<DrugInventoryDTO> getInventory(@PathVariable Long id) {
        DrugInventoryDTO dto = inventoryService.getInventory(id);
        return AuthResponse.success(dto);
    }

    /**
     * 调整库存（入库 / 出库 / 纠偏）
     * POST /api/pharmacist/inventory/adjust
     */
    @PostMapping("/inventory/adjust")
    public AuthResponse<Void> adjustInventory(@RequestBody InventoryAdjustRequest req) {
        inventoryService.adjustInventory(req);
        return AuthResponse.success(null);
    }

    // ================== 三、处方列表 / 发药 ==================

    /**
     * 处方列表（按日期 + 状态）
     * GET /api/pharmacist/prescriptions?date=2025-12-09&status=PENDING
     */
    @GetMapping("/prescriptions")
    public AuthResponse<List<PharmPrescriptionSummaryDTO>> listPrescriptions(
            @RequestParam(required = false) String date,    // yyyy-MM-dd
            @RequestParam(required = false) String status   // PENDING / DISPENSED
    ) {
        LocalDate d;
        if (date == null || date.isBlank()) {
            d = LocalDate.now();
        } else {
            d = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        }

        List<PharmPrescriptionSummaryDTO> list =
                pharmPrescriptionService.listPrescriptions(d, status);

        return AuthResponse.success(list);
    }

    /**
     * 处方详情
     * GET /api/pharmacist/prescriptions/{id}
     */
    @GetMapping("/prescriptions/{id}")
    public AuthResponse<PharmPrescriptionDetailDTO> getPrescriptionDetail(
            @PathVariable Long id
    ) {
        PharmPrescriptionDetailDTO dto = pharmPrescriptionService.getPrescriptionDetail(id);
        return AuthResponse.success(dto);
    }

    /**
     * 发药确认
     * POST /api/pharmacist/prescriptions/{id}/dispense
     */
    @PostMapping("/prescriptions/{id}/dispense")
    public AuthResponse<Void> dispense(
            @PathVariable Long id,
            @RequestBody DispenseRequest req
    ) {
        pharmPrescriptionService.dispense(id, req);
        return AuthResponse.success(null);
    }
}
