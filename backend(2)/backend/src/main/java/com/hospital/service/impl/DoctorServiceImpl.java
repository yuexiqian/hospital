package com.hospital.service.impl;

import com.hospital.dto.*;
import com.hospital.model.*;
import com.hospital.repository.*;
import com.hospital.service.BillService;
import com.hospital.service.DoctorService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DeptRepository deptRepository;
    private final RegisterRepository registerRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final DrugInfoRepository drugInfoRepository;

    // ✅ 新增：账单服务
    private final BillService billService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DoctorServiceImpl(DoctorRepository doctorRepository,
                             DeptRepository deptRepository,
                             RegisterRepository registerRepository,
                             PatientRepository patientRepository,
                             PrescriptionRepository prescriptionRepository,
                             PrescriptionItemRepository prescriptionItemRepository,
                             DrugInfoRepository drugInfoRepository,
                             BillService billService) {
        this.doctorRepository = doctorRepository;
        this.deptRepository = deptRepository;
        this.registerRepository = registerRepository;
        this.patientRepository = patientRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.drugInfoRepository = drugInfoRepository;
        this.billService = billService;
    }

    @Override
    public List<Doctor> listByDept(Long deptId) {
        return doctorRepository.findByDeptIdAndStatus(deptId, 1);
    }

    @Override
    public List<DoctorQueueItemDTO> getQueue(Long doctorId, LocalDate date) {
        if (date == null) date = LocalDate.now();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<RegisterRecord> list =
                registerRepository.findByDoctorIdAndRegisterTimeBetweenOrderByRegisterTimeAsc(
                        doctorId, start, end);

        Set<Long> patientIds = list.stream()
                .map(RegisterRecord::getPatientId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Patient> patientMap = patientRepository.findByPatientIdIn(patientIds)
                .stream()
                .collect(Collectors.toMap(Patient::getPatientId, p -> p));

        Set<Long> deptIds = list.stream()
                .map(RegisterRecord::getDeptId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> deptNameMap = deptRepository.findAllById(deptIds).stream()
                .collect(Collectors.toMap(Dept::getId, Dept::getName));

        List<DoctorQueueItemDTO> dtoList = new ArrayList<>();
        for (RegisterRecord r : list) {
            DoctorQueueItemDTO dto = new DoctorQueueItemDTO();
            dto.setId(r.getId());
            dto.setPatientId(r.getPatientId());

            Patient p = r.getPatientId() == null ? null : patientMap.get(r.getPatientId());
            dto.setPatientName(p != null ? p.getName() : "-");

            dto.setDeptId(r.getDeptId());

            String deptName = r.getDeptName();
            if ((deptName == null || deptName.isBlank()) && r.getDeptId() != null) {
                deptName = deptNameMap.get(r.getDeptId());
            }
            dto.setDeptName(deptName != null ? deptName : "-");

            LocalDateTime rt = r.getRegisterTime();
            dto.setRegisterTime(rt != null ? rt.toLocalTime().format(TIME_FMT) : "");

            Integer qs = r.getQueueStatus();
            dto.setQueueStatus(qs == null ? 0 : qs);

            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public void startVisit(Long registerId) {
        RegisterRecord r = registerRepository.findById(registerId)
                .orElseThrow(() -> new RuntimeException("挂号记录不存在"));

        Integer qs = r.getQueueStatus();
        if (qs == null) qs = 0;

        if (qs == 3 || qs == 4 || qs == 9) {
            throw new RuntimeException("当前状态不允许开始就诊");
        }
        if (qs == 2) return; // 幂等：已就诊中

        r.setQueueStatus(2);
        r.setUpdateTime(LocalDateTime.now());
        registerRepository.save(r);
    }

    @Override
    public void finishVisit(Long registerId) {
        RegisterRecord r = registerRepository.findById(registerId)
                .orElseThrow(() -> new RuntimeException("挂号记录不存在"));

        Integer qs = r.getQueueStatus();
        if (qs == null) qs = 0;

        if (qs == 3) return;
        if (qs != 2) throw new RuntimeException("当前状态不允许结束就诊");

        r.setQueueStatus(3);
        r.setStatus("FINISHED");
        r.setUpdateTime(LocalDateTime.now());
        registerRepository.save(r);
    }

    @Override
    public DoctorVisitDetailDTO getVisitDetail(Long registerId) {
        RegisterRecord r = registerRepository.findById(registerId)
                .orElseThrow(() -> new RuntimeException("挂号记录不存在"));

        DoctorVisitDetailDTO dto = new DoctorVisitDetailDTO();
        dto.setRegisterId(r.getId());

        LocalDateTime rt = r.getRegisterTime();
        if (rt != null) {
            dto.setRegisterDate(rt.toLocalDate().format(DATE_FMT));
            dto.setRegisterTime(rt.toLocalTime().format(TIME_FMT));
        }

        dto.setVisitStatus(r.getStatus());
        dto.setPatientId(r.getPatientId());
        dto.setDeptId(r.getDeptId());
        dto.setDeptName(r.getDeptName());
        dto.setDoctorId(r.getDoctorId());
        dto.setDoctorName(r.getDoctorName());

        if (r.getPatientId() != null) {
            patientRepository.findById(r.getPatientId()).ifPresent(p -> {
                dto.setPatientName(p.getName());
                dto.setIdCard(p.getIdCard());
                dto.setPhone(p.getPhone());
            });
        }

        if (r.getDoctorId() != null) {
            doctorRepository.findById(r.getDoctorId())
                    .ifPresent(d -> dto.setDoctorTitle(d.getTitle()));
        }

        List<Prescription> presList = prescriptionRepository.findByRegisterIdOrderByIdAsc(registerId);

        List<DoctorVisitDetailDTO.PrescriptionDTO> presDtos = new ArrayList<>();
        for (Prescription p : presList) {
            DoctorVisitDetailDTO.PrescriptionDTO pd = new DoctorVisitDetailDTO.PrescriptionDTO();
            pd.setPrescriptionId(p.getId());
            pd.setStatus(p.getStatus());

            if (dto.getDiag() == null || dto.getDiag().isEmpty()) {
                dto.setDiag(p.getDiag());
            }

            List<PrescriptionItem> items = prescriptionItemRepository.findByPrescriptionId(p.getId());
            List<DoctorVisitDetailDTO.PrescriptionItemDTO> itemDtos = new ArrayList<>();
            for (PrescriptionItem it : items) {
                DoctorVisitDetailDTO.PrescriptionItemDTO idto = new DoctorVisitDetailDTO.PrescriptionItemDTO();
                idto.setItemId(it.getId());
                idto.setDrugId(it.getDrugId());
                idto.setDosage(it.getDosage());
                idto.setFrequency(it.getFrequency());
                idto.setDays(it.getDays());
                idto.setQuantity(it.getQuantity());
                idto.setRemark(it.getRemark());

                if (it.getDrugId() != null) {
                    drugInfoRepository.findById(it.getDrugId())
                            .ifPresent(di -> idto.setDrugName(di.getDrugName()));
                }
                itemDtos.add(idto);
            }
            pd.setItems(itemDtos);
            presDtos.add(pd);
        }

        dto.setPrescriptions(presDtos);
        dto.setRemark(r.getRemark());
        return dto;
    }

    @Override
    public void saveVisit(Long registerId, DoctorVisitSaveRequest req) {
        RegisterRecord r = registerRepository.findById(registerId)
                .orElseThrow(() -> new RuntimeException("挂号记录不存在"));

        boolean submit = req.isSubmitPrescription();

        Integer qs = r.getQueueStatus() == null ? 0 : r.getQueueStatus();
        if (qs == 3 || qs == 4 || qs == 9) {
            throw new RuntimeException("该挂号记录已结束，不能继续保存");
        }

        r.setRemark(req.getRemark());
        r.setUpdateTime(LocalDateTime.now());

        if (submit) {
            r.setQueueStatus(3);
            r.setStatus("FINISHED");
        } else {
            if (qs < 2) r.setQueueStatus(2);
        }
        registerRepository.save(r);

        if (req.getPrescriptions() == null || req.getPrescriptions().isEmpty()) {
            return;
        }

        DoctorVisitSaveRequest.PrescriptionDTO pReq = req.getPrescriptions().get(0);

        Prescription p;
        if (pReq.getPrescriptionId() == null) {
            p = new Prescription();
            p.setRegisterId(registerId);
            p.setPatientId(r.getPatientId());
            p.setDoctorId(r.getDoctorId());
            p.setCreateTime(LocalDateTime.now());
            p.setTotalAmount(BigDecimal.ZERO);
        } else {
            p = prescriptionRepository.findById(pReq.getPrescriptionId())
                    .orElseThrow(() -> new RuntimeException("处方不存在"));
            if (p.getTotalAmount() == null) p.setTotalAmount(BigDecimal.ZERO);
        }

        p.setDiag(req.getDiag());
        p.setStatus(submit ? "SUBMITTED" : "DRAFT");
        p = prescriptionRepository.save(p);

        // 清空旧明细再插入
        prescriptionItemRepository.deleteByPrescriptionId(p.getId());

        BigDecimal total = BigDecimal.ZERO;
        List<PrescriptionItem> savedItemsForBill = new ArrayList<>();

        if (pReq.getItems() != null) {
            for (DoctorVisitSaveRequest.PrescriptionItemDTO itReq : pReq.getItems()) {
                if (itReq.getDrugId() == null || itReq.getQuantity() == null) continue;

                PrescriptionItem it = new PrescriptionItem();
                it.setPrescriptionId(p.getId());
                it.setDrugId(itReq.getDrugId());
                it.setQuantity(itReq.getQuantity());
                it.setDosage(itReq.getDosage());
                it.setFrequency(itReq.getFrequency());
                it.setDays(itReq.getDays());
                it.setRemark(itReq.getRemark());

                BigDecimal amount = itReq.getAmount() != null ? itReq.getAmount() : BigDecimal.ZERO;
                it.setAmount(amount);
                total = total.add(amount);

                it = prescriptionItemRepository.save(it);
                savedItemsForBill.add(it);
            }
        }

        p.setTotalAmount(total);
        p = prescriptionRepository.save(p);

        // ✅✅✅ 核心：提交处方时生成待缴账单
        if (submit) {
            billService.upsertPrescriptionBill(r, p, savedItemsForBill);
        }
    }

    @Override
    public List<DoctorVisitHistoryItemDTO> getPatientHistory(Long patientId) {
        List<RegisterRecord> list = registerRepository.findByPatientIdOrderByRegisterTimeDesc(patientId);

        List<DoctorVisitHistoryItemDTO> res = new ArrayList<>();
        for (RegisterRecord r : list) {
            DoctorVisitHistoryItemDTO dto = new DoctorVisitHistoryItemDTO();
            dto.setRegisterId(r.getId());
            LocalDateTime rt = r.getRegisterTime();
            dto.setVisitTime(rt != null ? rt.format(DATETIME_FMT) : "");
            dto.setDeptName(r.getDeptName());
            dto.setDoctorName(r.getDoctorName());

            Prescription p = prescriptionRepository
                    .findFirstByRegisterIdOrderByIdAsc(r.getId())
                    .orElse(null);
            dto.setDiag(p != null ? p.getDiag() : "");
            res.add(dto);
        }
        return res;
    }

    @Override
    public List<DoctorDrugSimpleDTO> searchDrugs(String keyword) {
        List<DrugInfo> list;
        if (keyword == null || keyword.trim().isEmpty()) {
            list = drugInfoRepository.findAll();
        } else {
            list = drugInfoRepository.searchByKeyword(keyword.trim());
        }
        List<DoctorDrugSimpleDTO> res = new ArrayList<>();
        for (DrugInfo d : list) {
            DoctorDrugSimpleDTO dto = new DoctorDrugSimpleDTO();
            dto.setDrugId(d.getId());
            dto.setDrugName(d.getDrugName());
            dto.setSpec(d.getSpec());
            dto.setDosageUsage(d.getDosageUsage());
            res.add(dto);
        }
        return res;
    }
}
