package com.hospital.service.impl;

import com.hospital.dto.DrugInventoryDTO;
import com.hospital.dto.InventoryAdjustRequest;
import com.hospital.model.DrugInfo;
import com.hospital.model.DrugInventory;
import com.hospital.repository.DrugInfoRepository;
import com.hospital.repository.DrugInventoryRepository;
import com.hospital.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 药品库存服务实现
 */
@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final DrugInventoryRepository inventoryRepository;
    private final DrugInfoRepository drugInfoRepository;

    public InventoryServiceImpl(DrugInventoryRepository inventoryRepository,
                                DrugInfoRepository drugInfoRepository) {
        this.inventoryRepository = inventoryRepository;
        this.drugInfoRepository = drugInfoRepository;
    }

    // ================== 对外接口实现 ==================

    @Override
    public List<DrugInventoryDTO> listInventories() {
        List<DrugInventory> list = inventoryRepository.findAll();
        return list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DrugInventoryDTO getInventory(Long id) {
        DrugInventory inv = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("库存记录不存在，ID=" + id));
        return toDto(inv);
    }

    @Override
    public void adjustInventory(InventoryAdjustRequest req) {
        if (req == null || req.getInventoryId() == null) {
            throw new IllegalArgumentException("inventoryId 不能为空");
        }
        DrugInventory inv = inventoryRepository.findById(req.getInventoryId())
                .orElseThrow(() -> new RuntimeException("库存记录不存在，ID=" + req.getInventoryId()));

        // changeQty 可以为正（入库）也可以为负（出库 / 纠偏）
        Integer change = req.getChangeQty();
        if (change == null || change == 0) {
            return;
        }

        int current = inv.getStockQty() == null ? 0 : inv.getStockQty();
        int after = current + change;
        if (after < 0) {
            throw new RuntimeException("库存不足，当前数量=" + current + "，变更数量=" + change);
        }

        inv.setStockQty(after);
        inv.setUpdateTime(LocalDateTime.now());
        inventoryRepository.save(inv);
    }

    // ================== 私有工具方法 ==================

    private DrugInventoryDTO toDto(DrugInventory inv) {
        DrugInventoryDTO dto = new DrugInventoryDTO();
        dto.setId(inv.getId());
        dto.setDrugId(inv.getDrugId());
        dto.setStockQty(inv.getStockQty());
        dto.setUnitPrice(inv.getUnitPrice());
        dto.setBatchNo(inv.getBatchNo());
        dto.setExpireDate(inv.getExpireDate());
        dto.setStatus(inv.getStatus());
        dto.setCreateTime(inv.getCreateTime());
        dto.setUpdateTime(inv.getUpdateTime());

        // 补充一点药品基础信息，方便前端显示
        if (inv.getDrugId() != null) {
            drugInfoRepository.findById(inv.getDrugId()).ifPresent(drug -> {
                dto.setDrugName(drug.getDrugName());
                dto.setGenericName(drug.getGenericName());
                dto.setSpec(drug.getSpec());
                dto.setDosageForm(drug.getDosageForm());
                dto.setCategory(drug.getCategory());
            });
        }

        return dto;
    }
}
