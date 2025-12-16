// src/main/java/com/hospital/controller/NurseQueueController.java
package com.hospital.controller;

import com.hospital.dto.AuthResponse;
import com.hospital.dto.NurseQueueItemDTO;
import com.hospital.dto.NurseTriageRequest;
import com.hospital.service.NurseQueueService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/nurse/queue")
@CrossOrigin
public class NurseQueueController {

    private final NurseQueueService nurseQueueService;

    public NurseQueueController(NurseQueueService nurseQueueService) {
        this.nurseQueueService = nurseQueueService;
    }

    /**
     * 查询候诊队列列表
     */
    @GetMapping("/list")
    public AuthResponse<List<NurseQueueItemDTO>> listQueue(
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate visitDate,
            @RequestParam(required = false) Integer queueStatus
    ) {
        List<NurseQueueItemDTO> list =
                nurseQueueService.listQueue(deptId, doctorId, visitDate, queueStatus);
        return AuthResponse.success(list);
    }

    /**
     * 护士叫号
     */
    @PostMapping("/{id}/call")
    public AuthResponse<Void> callPatient(@PathVariable("id") Long id,
                                          @RequestParam Long nurseId) {
        nurseQueueService.callPatient(id, nurseId);
        return AuthResponse.success(null);
    }

    /**
     * 标记过号
     */
    @PostMapping("/{id}/markNoShow")
    public AuthResponse<Void> markNoShow(@PathVariable("id") Long id) {
        nurseQueueService.markNoShow(id);
        return AuthResponse.success(null);
    }

    /**
     * 设置加急
     */
    @PostMapping("/{id}/setPriority")
    public AuthResponse<Void> setPriority(@PathVariable("id") Long id,
                                          @RequestParam(required = false) Integer priority) {
        nurseQueueService.setPriority(id, priority);
        return AuthResponse.success(null);
    }

    /**
     * 取消加急
     */
    @PostMapping("/{id}/cancelPriority")
    public AuthResponse<Void> cancelPriority(@PathVariable("id") Long id) {
        nurseQueueService.cancelPriority(id);
        return AuthResponse.success(null);
    }

    /**
     * 保存分诊信息
     */
    @PostMapping("/{id}/triage")
    public AuthResponse<Void> saveTriage(@PathVariable("id") Long id,
                                         @RequestBody NurseTriageRequest triageRequest) {
        nurseQueueService.saveTriageInfo(id, triageRequest);
        return AuthResponse.success(null);
    }
}
