package com.hospital.service.admin;

import com.hospital.dto.admin.AdminRegisterListItemDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface AdminRegisterService {
    Page<AdminRegisterListItemDTO> page(
            LocalDate date,
            Long deptId,
            Long doctorId,
            String status,        // 建议先用 String（见下面第3点）
            Integer queueStatus,
            int page,
            int size
    );

    void fixQueueStatus(Long registerId, Integer value);
    void setPriority(Long registerId, Integer value);
}
