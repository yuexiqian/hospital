package com.hospital.controller;

import com.hospital.dto.admin.StaffOptionDTO;
import com.hospital.model.Doctor;
import com.hospital.model.Nurse;
import com.hospital.model.Pharmacist;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.NurseRepository;
import com.hospital.repository.PharmacistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/options")
@RequiredArgsConstructor
public class AdminStaffOptionsController {

    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final PharmacistRepository pharmacistRepository;

    @GetMapping("/doctors")
    public List<StaffOptionDTO> doctorOptions() {
        return doctorRepository.findAll().stream().map(d -> {
            StaffOptionDTO o = new StaffOptionDTO();
            o.setId(d.getId());
            o.setName(d.getName());
            o.setDeptId(d.getDeptId());
            o.setStatusOrActive(d.getStatus());
            o.setUserId(d.getUserId());
            return o;
        }).toList();
    }

    @GetMapping("/nurses")
    public List<StaffOptionDTO> nurseOptions() {
        return nurseRepository.findAll().stream().map(n -> {
            StaffOptionDTO o = new StaffOptionDTO();
            o.setId(n.getId());
            o.setName(n.getName());
            o.setDeptId(n.getDeptId());
            o.setStatusOrActive(n.getIsActive());
            o.setUserId(n.getUserId());
            return o;
        }).toList();
    }

    @GetMapping("/pharmacists")
    public List<StaffOptionDTO> pharmacistOptions() {
        return pharmacistRepository.findAll().stream().map(p -> {
            StaffOptionDTO o = new StaffOptionDTO();
            o.setId(p.getId());
            o.setName(p.getName());
            o.setDeptId(p.getDeptId());
            o.setStatusOrActive(p.getIsActive());
            o.setUserId(p.getUserId());
            return o;
        }).toList();
    }
}
