package com.hospital.service;

import com.hospital.model.MedicationGuide;

import java.util.Optional;

public interface MedicationGuideService {

    Optional<MedicationGuide> getLatestByUserId(Long userId);

    MedicationGuide save(MedicationGuide guide);
}
