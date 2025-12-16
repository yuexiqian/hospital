// src/main/java/com/hospital/service/PharmPrescriptionService.java
package com.hospital.service;

import com.hospital.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface PharmPrescriptionService {

    List<PharmPrescriptionSummaryDTO> listPrescriptions(
            LocalDate date,
            String status  // 可为 null
    );

    PharmPrescriptionDetailDTO getPrescriptionDetail(Long id);

    void dispense(Long id, DispenseRequest req);
}
