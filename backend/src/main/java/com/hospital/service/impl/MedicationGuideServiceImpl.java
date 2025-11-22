package com.hospital.service.impl;

import com.hospital.model.MedicationGuide;
import com.hospital.repository.MedicationGuideRepository;
import com.hospital.service.MedicationGuideService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MedicationGuideServiceImpl implements MedicationGuideService {

    private final MedicationGuideRepository repository;

    public MedicationGuideServiceImpl(MedicationGuideRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<MedicationGuide> getLatestByUserId(Long userId) {
        return repository.findFirstByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public MedicationGuide save(MedicationGuide guide) {
        return repository.save(guide);
    }
}
