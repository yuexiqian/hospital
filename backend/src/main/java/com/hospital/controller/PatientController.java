package com.hospital.controller;

import com.hospital.dto.PatientRequest;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.service.PatientService;
import com.hospital.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final UserService userService;

    @GetMapping
    public List<Patient> list(@RequestHeader("X-USER-ID") Long userId) {
        User owner = userService.getById(userId);
        return patientService.list(owner);
    }

    @PostMapping
    public Patient create(@RequestHeader("X-USER-ID") Long userId,
                          @Valid @RequestBody PatientRequest request) {
        User owner = userService.getById(userId);
        request.setId(null);
        return patientService.upsert(owner, request);
    }

    @PutMapping("/{id}")
    public Patient update(@RequestHeader("X-USER-ID") Long userId,
                          @PathVariable Long id,
                          @Valid @RequestBody PatientRequest request) {
        User owner = userService.getById(userId);
        request.setId(id);
        return patientService.upsert(owner, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }
}

