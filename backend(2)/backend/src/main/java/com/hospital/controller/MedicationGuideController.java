package com.hospital.controller;

import com.hospital.model.MedicationGuide;
import com.hospital.service.MedicationGuideService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medication-guides")
@CrossOrigin
public class MedicationGuideController {

    private final MedicationGuideService medicationGuideService;

    public MedicationGuideController(MedicationGuideService medicationGuideService) {
        this.medicationGuideService = medicationGuideService;
    }

    /**
     * 用药指导列表（给侧边栏单独页面用）
     * GET /api/medication-guides?userId=1
     */
    @GetMapping
    public List<MedicationGuide> listByUser(@RequestParam Long userId) {
        return medicationGuideService.listByUserId(userId);
    }

    /**
     * （可选）根据 ID 获取详情
     */
    @GetMapping("/{id}")
    public MedicationGuide getOne(@PathVariable Long id) {
        return medicationGuideService
                .save(medicationGuideService
                        .getLatestByUserId(id)
                        .orElseThrow(() -> new RuntimeException("not found")));
    }
}
