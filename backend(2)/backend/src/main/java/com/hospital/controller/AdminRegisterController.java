package com.hospital.controller;

import com.hospital.dto.admin.AdminRegisterListItemDTO;      // ✅ 必须有
import com.hospital.service.admin.AdminRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/registers")
@RequiredArgsConstructor
public class AdminRegisterController {

    private final AdminRegisterService adminRegisterService;

    @GetMapping
    public Page<AdminRegisterListItemDTO> page(
            @RequestParam(required = false) String date,      // yyyy-MM-dd
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String status,    // ✅ String（对齐 service/DTO）
            @RequestParam(required = false) Integer queueStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        LocalDate d = (date == null || date.isBlank()) ? null : LocalDate.parse(date);
        return adminRegisterService.page(d, deptId, doctorId, status, queueStatus, page, size);
    }

    @PostMapping("/{id}/fixQueueStatus")
    public void fixQueueStatus(@PathVariable("id") Long id,
                               @RequestParam Integer value) {
        adminRegisterService.fixQueueStatus(id, value);
    }

    @PostMapping("/{id}/priority")
    public void setPriority(@PathVariable("id") Long id,
                            @RequestParam Integer value) {
        adminRegisterService.setPriority(id, value);
    }
}
