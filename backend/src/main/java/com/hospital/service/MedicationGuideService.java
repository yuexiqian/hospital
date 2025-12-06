package com.hospital.service;

import com.hospital.model.MedicationGuide;

import java.util.List;
import java.util.Optional;

public interface MedicationGuideService {

    Optional<MedicationGuide> getLatestByUserId(Long userId);

    MedicationGuide save(MedicationGuide guide);

    /** 新增：给用药页面用的列表查询 */
    List<MedicationGuide> listByUserId(Long userId);
}
