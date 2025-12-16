package com.hospital.service;

import com.hospital.dto.DrugInventoryDTO;
import com.hospital.dto.InventoryAdjustRequest;

import java.util.List;

public interface InventoryService {

    // 库存列表
    List<DrugInventoryDTO> listInventories();

    // 查看单条库存
    DrugInventoryDTO getInventory(Long id);

    // 调整库存
    void adjustInventory(InventoryAdjustRequest req);
}
