// src/main/java/com/hospital/controller/DoctorController.java
package com.hospital.controller;

import com.hospital.dto.*;
import com.hospital.model.Doctor;
import com.hospital.service.DoctorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // ============ 挂号时按科室列出医生 ============

    @GetMapping("/by-dept")
    public List<Doctor> listByDept(@RequestParam Long deptId) {
        return doctorService.listByDept(deptId);
    }

    // ============ 候诊队列 ============

    @GetMapping("/queue")
    public List<DoctorQueueItemDTO> getQueue(@RequestParam Long doctorId,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd")
                                             LocalDate date) {
        return doctorService.getQueue(doctorId, date);
    }

    @PostMapping("/queue/{registerId}/start")
    public void startVisit(@PathVariable Long registerId) {
        doctorService.startVisit(registerId);
    }

    @PostMapping("/queue/{registerId}/finish")
    public void finishVisit(@PathVariable Long registerId) {
        doctorService.finishVisit(registerId);
    }

    // ============ 就诊详情 / 保存 ============

    @GetMapping("/visit/{registerId}")
    public DoctorVisitDetailDTO getVisitDetail(@PathVariable Long registerId) {
        return doctorService.getVisitDetail(registerId);
    }

    @PostMapping("/visit/{registerId}/save")
    public void saveVisit(@PathVariable Long registerId,
                          @RequestBody DoctorVisitSaveRequest req) {
        doctorService.saveVisit(registerId, req);
    }

    // ============ 患者历史记录 ============

    @GetMapping("/patient/{patientId}/history")
    public List<DoctorVisitHistoryItemDTO> getPatientHistory(@PathVariable Long patientId) {
        return doctorService.getPatientHistory(patientId);
    }

    // ============ 药品搜索（医生端） ============

    @GetMapping("/drug-lib")
    public List<DoctorDrugSimpleDTO> searchDrugs(@RequestParam(required = false) String keyword) {
        return doctorService.searchDrugs(keyword);
    }
}
