package com.hospital.service.admin.impl;

import com.hospital.dto.admin.AdminRegisterListItemDTO;
import com.hospital.model.RegisterRecord;
import com.hospital.repository.RegisterRepository;
import com.hospital.service.admin.AdminRegisterService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRegisterServiceImpl implements AdminRegisterService {

    private final RegisterRepository registerRepository;

    @Override
    public Page<AdminRegisterListItemDTO> page(LocalDate date,
                                               Long deptId,
                                               Long doctorId,
                                               String status,          // ✅ String
                                               Integer queueStatus,
                                               int page, int size) {

        LocalDate d = (date == null) ? LocalDate.now() : date;
        LocalDateTime start = d.atStartOfDay();
        LocalDateTime end = d.plusDays(1).atStartOfDay();

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 10),
                Sort.by(Sort.Direction.DESC, "registerTime"));

        Specification<RegisterRecord> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.greaterThanOrEqualTo(root.get("registerTime"), start));
            ps.add(cb.lessThan(root.get("registerTime"), end));

            if (deptId != null) ps.add(cb.equal(root.get("deptId"), deptId));
            if (doctorId != null) ps.add(cb.equal(root.get("doctorId"), doctorId));
            if (status != null && !status.isBlank()) ps.add(cb.equal(root.get("status"), status)); // ✅ String
            if (queueStatus != null) ps.add(cb.equal(root.get("queueStatus"), queueStatus));

            return cb.and(ps.toArray(new Predicate[0]));
        };

        Page<RegisterRecord> p = registerRepository.findAll(spec, pageable);

        List<AdminRegisterListItemDTO> rows = new ArrayList<>();
        for (RegisterRecord r : p.getContent()) rows.add(toDTO(r));
        return new PageImpl<>(rows, pageable, p.getTotalElements());
    }

    @Override
    @Transactional
    public void fixQueueStatus(Long registerId, Integer value) {
        RegisterRecord r = registerRepository.findById(registerId)
                .orElseThrow(() -> new RuntimeException("挂号记录不存在"));
        r.setQueueStatus(value);
        registerRepository.save(r);
    }

    @Override
    @Transactional
    public void setPriority(Long registerId, Integer value) {
        RegisterRecord r = registerRepository.findById(registerId)
                .orElseThrow(() -> new RuntimeException("挂号记录不存在"));
        r.setQueuePriority(value);
        registerRepository.save(r);
    }

    private AdminRegisterListItemDTO toDTO(RegisterRecord r) {
        AdminRegisterListItemDTO dto = new AdminRegisterListItemDTO();
        dto.setId(r.getId());
        dto.setUserId(r.getUserId());
        dto.setPatientId(r.getPatientId());
        dto.setDeptId(r.getDeptId());
        dto.setDeptName(r.getDeptName());
        dto.setDoctorId(r.getDoctorId());
        dto.setDoctorName(r.getDoctorName());
        dto.setRegisterTime(r.getRegisterTime());
        dto.setQueueNo(r.getQueueNo());

        dto.setStatus(r.getStatus());                 // ✅ String -> String
        dto.setQueueStatus(r.getQueueStatus());
        dto.setQueuePriority(r.getQueuePriority());

        dto.setTriageNurseId(r.getTriageNurseId());
        dto.setTriageTime(r.getTriageTime());
        dto.setTriageNote(r.getTriageNote());
        dto.setLastCallTime(r.getLastCallTime());
        dto.setCalledTimes(r.getCalledTimes());
        return dto;
    }
}
