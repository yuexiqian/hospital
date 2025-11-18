package com.hospital.service;

import com.hospital.dto.PatientRequest;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<Patient> list(User owner) {
        return patientRepository.findByOwner(owner);
    }

    public Patient upsert(User owner, PatientRequest request) {
        Patient patient = request.getId() == null
                ? new Patient()
                : patientRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("就诊人不存在"));
        patient.setName(request.getName());
        patient.setGender(request.getGender());
        patient.setCertificateType(request.getCertificateType());
        patient.setCertificateNo(request.getCertificateNo());
        patient.setRelationship(request.getRelationship());
        patient.setContactPhone(request.getContactPhone());
        patient.setOwner(owner);
        return patientRepository.save(patient);
    }

    public void delete(Long id) {
        patientRepository.deleteById(id);
    }
}

