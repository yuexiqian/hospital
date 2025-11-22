package com.hospital.controller;

import com.hospital.dto.BillsSummaryDTO;
import com.hospital.dto.HomeOverviewDTO;
import com.hospital.dto.MedicationCardDTO;
import com.hospital.dto.QueueStatusDTO;
import com.hospital.dto.RegisterInfoDTO;
import com.hospital.dto.TodayRegisterCardDTO;
import com.hospital.model.Dept;
import com.hospital.model.Doctor;
import com.hospital.model.MedicationGuide;
import com.hospital.repository.DeptRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.service.BillService;
import com.hospital.service.MedicationGuideService;
import com.hospital.service.RegisterService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/home")
@CrossOrigin
public class HomeOverviewController {

    private final RegisterService registerService;
    private final MedicationGuideService medicationGuideService;
    private final BillService billService;
    private final DeptRepository deptRepository;
    private final DoctorRepository doctorRepository;

    public HomeOverviewController(RegisterService registerService,
                                  MedicationGuideService medicationGuideService,
                                  BillService billService,
                                  DeptRepository deptRepository,
                                  DoctorRepository doctorRepository) {
        this.registerService = registerService;
        this.medicationGuideService = medicationGuideService;
        this.billService = billService;
        this.deptRepository = deptRepository;
        this.doctorRepository = doctorRepository;
    }

    /**
     * 患者首页 A 区总接口
     * GET /api/home/overview?userId=1
     */
    @GetMapping("/overview")
    public HomeOverviewDTO getOverview(@RequestParam Long userId) {
        HomeOverviewDTO overview = new HomeOverviewDTO();

        // ================= A1：今日挂号卡片 =================
        RegisterInfoDTO latestRegister = registerService.getLatestTodayRegister(userId);
        TodayRegisterCardDTO todayCard = new TodayRegisterCardDTO();

        if (latestRegister != null) {
            // 补全科室名 & 医生名
            fillDeptAndDoctorNames(latestRegister);

            todayCard.setHasRegister(true);
            todayCard.setRegisterId(latestRegister.getId());
            todayCard.setDeptName(latestRegister.getDeptName());
            todayCard.setDoctorName(latestRegister.getDoctorName());
            todayCard.setQueueNo(latestRegister.getQueueNo());
            todayCard.setStatus(latestRegister.getStatus());
            todayCard.setRegisterTime(latestRegister.getRegisterTime());
        } else {
            todayCard.setHasRegister(false);
        }
        overview.setTodayRegister(todayCard);

        // ================= A2：当前候诊情况 =================
        RegisterInfoDTO waitingRegister = registerService.getLatestWaitingTodayRegister(userId);
        QueueStatusDTO queueStatus = new QueueStatusDTO();

        if (waitingRegister != null) {
            fillDeptAndDoctorNames(waitingRegister);

            queueStatus.setHasWaiting(true);
            queueStatus.setDeptName(waitingRegister.getDeptName());
            queueStatus.setDoctorName(waitingRegister.getDoctorName());
            queueStatus.setAheadCount(
                    waitingRegister.getAheadCount() != null ? waitingRegister.getAheadCount() : 0
            );
            queueStatus.setEstimateWaitMin(
                    waitingRegister.getEstimateWaitMin() != null
                            ? waitingRegister.getEstimateWaitMin()
                            : 0
            );
        } else {
            queueStatus.setHasWaiting(false);
        }
        overview.setQueueStatus(queueStatus);

        // ================= A3：最近用药指导卡片 =================
        // 使用你已有的 getLatestByUserId 方法，查 medication_guide 表
        MedicationCardDTO medicationCard = new MedicationCardDTO();
        Optional<MedicationGuide> guideOpt = medicationGuideService.getLatestByUserId(userId);
        if (guideOpt.isPresent()) {
            MedicationGuide g = guideOpt.get();
            // 下面这些 setter 名字要和你的 MedicationCardDTO 字段对应，
            // 如果不同，就按你的字段名改。
            medicationCard.setHasGuide(true);                 // 是否有用药指导
            medicationCard.setGuideId(g.getId());             // 指导ID
            medicationCard.setDeptName(g.getDeptName());      // 科室
            medicationCard.setDoctorName(g.getDoctorName());  // 医生
            medicationCard.setTitle(g.getTitle());            // 标题
            medicationCard.setContentPreview(g.getContent()); // 简单先放全文
            medicationCard.setViewed(g.getViewed());          // 是否已查看
            medicationCard.setCreateTime(g.getCreateTime());  // 创建时间
        } else {
            // 没有记录时
            medicationCard.setHasGuide(false);
        }
        overview.setMedication(medicationCard);

        // ================= A4：待缴费摘要卡片 =================
        BillsSummaryDTO billsSummary = new BillsSummaryDTO();
        long unpaidCount = billService.countUnpaidByUser(userId);
        BigDecimal unpaidAmount = billService.sumUnpaidAmountByUser(userId);
        if (unpaidAmount == null) {
            unpaidAmount = BigDecimal.ZERO;
        }

        // 同样，这里的字段名要和你的 BillsSummaryDTO 对应
        billsSummary.setUnpaidCount(unpaidCount);
        billsSummary.setUnpaidAmount(unpaidAmount);

        overview.setBills(billsSummary);

        return overview;
    }

    /**
     * 根据 deptId / doctorId 补全科室名和医生名
     */
    private void fillDeptAndDoctorNames(RegisterInfoDTO info) {
        if (info == null) {
            return;
        }

        // 查科室名
        if (info.getDeptId() != null) {
            deptRepository.findById(info.getDeptId())
                    .ifPresent(dept -> info.setDeptName(dept.getName()));
        }

        // 查医生名
        if (info.getDoctorId() != null) {
            doctorRepository.findById(info.getDoctorId())
                    .ifPresent(doc -> info.setDoctorName(doc.getName()));
        }
    }
}
