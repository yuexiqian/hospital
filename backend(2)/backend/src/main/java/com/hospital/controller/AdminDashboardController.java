package com.hospital.controller;

import com.hospital.dto.admin.DashboardSummaryDTO;
import com.hospital.dto.admin.TrendPointDTO;
import com.hospital.repository.RegisterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final RegisterRepository registerRepository;

    @GetMapping("/summary")
    public DashboardSummaryDTO summary(@RequestParam(required = false) String date) {
        LocalDate d = (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
        LocalDateTime start = d.atStartOfDay();
        LocalDateTime end = d.plusDays(1).atStartOfDay();

        DashboardSummaryDTO dto = new DashboardSummaryDTO();
        dto.setDate(d.toString());

        // 总数
        dto.setRegisterTotal(registerRepository.count((root, q, cb) ->
                cb.and(
                        cb.greaterThanOrEqualTo(root.get("registerTime"), start),
                        cb.lessThan(root.get("registerTime"), end)
                )
        ));

        // 已分诊数
        dto.setTriaged(registerRepository.countTriaged(start, end));

        // 按 queue_status 分组
        var qs = registerRepository.countByQueueStatus(start, end);
        for (var x : qs) {
            DashboardSummaryDTO.QueueStatusCountDTO t = new DashboardSummaryDTO.QueueStatusCountDTO();
            t.setQueueStatus(x.getQueueStatus());
            t.setCnt(x.getCnt());
            dto.getByQueueStatus().add(t);
        }

        // 科室Top
        var ds = registerRepository.countByDept(start, end);
        for (var x : ds) {
            DashboardSummaryDTO.DeptCountDTO t = new DashboardSummaryDTO.DeptCountDTO();
            t.setDeptId(x.getDeptId());
            t.setDeptName(x.getDeptName());
            t.setCnt(x.getCnt());
            dto.getByDept().add(t);
        }

        return dto;
    }

    @GetMapping("/trend")
    public List<TrendPointDTO> trend(@RequestParam(defaultValue = "7") int days) {
        int d = Math.max(1, Math.min(days, 90));
        LocalDate endDay = LocalDate.now().plusDays(1);
        LocalDate startDay = endDay.minusDays(d);
        LocalDateTime start = startDay.atStartOfDay();
        LocalDateTime end = endDay.atStartOfDay();

        var rows = registerRepository.countByDay(start, end);
        List<TrendPointDTO> out = new ArrayList<>();
        for (var r : rows) {
            TrendPointDTO t = new TrendPointDTO();
            t.setDay(r.getDay().toString());
            t.setCnt(r.getCnt());
            out.add(t);
        }
        return out;
    }
}
