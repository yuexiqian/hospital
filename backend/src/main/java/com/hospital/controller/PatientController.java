package com.hospital.controller;

import com.hospital.model.Patient;
import com.hospital.service.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:5173")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * 查询某个用户的全部就诊人
     * GET /api/patients?userId=1
     */
    @GetMapping
    public List<Patient> listMyPatients(@RequestParam("userId") Long userId) {
        return patientService.listByUser(userId);
    }

    /**
     * 新增就诊人
     * POST /api/patients?userId=1
     */
    @PostMapping
    public Patient createPatient(@RequestParam("userId") Long userId,
                                 @RequestBody Patient patient) {
        return patientService.createPatient(userId, patient);
    }

    /**
     * 修改就诊人
     * PUT /api/patients/{id}?userId=1
     */
    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable("id") Long patientId,
                                 @RequestParam("userId") Long userId,
                                 @RequestBody Patient patient) {
        return patientService.updatePatient(userId, patientId, patient);
    }

    /**
     * 删除就诊人
     * DELETE /api/patients/{id}?userId=1
     */
    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable("id") Long patientId,
                              @RequestParam("userId") Long userId) {
        patientService.deletePatient(userId, patientId);
    }
}
