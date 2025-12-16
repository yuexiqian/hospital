package com.hospital.service;

import com.hospital.dto.DrugInfoDTO;
import com.hospital.dto.DrugInfoUpdateRequest;

import java.util.List;

public interface PharmacistService {

    List<DrugInfoDTO> searchDrugInfos(String keyword);

    DrugInfoDTO getDrugInfo(Long id);

    void updateDrugInfo(Long id, DrugInfoUpdateRequest req);

    // ⭐ 新增：创建一个新的药品知识
    DrugInfoDTO createDrugInfo(DrugInfoUpdateRequest req);
}
