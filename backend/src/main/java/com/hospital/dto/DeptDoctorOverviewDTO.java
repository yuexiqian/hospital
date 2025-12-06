package com.hospital.dto;

import lombok.Data;

import java.util.List;

/**
 * 首页分诊区域上方：科室介绍 + 医生列表总览
 */
@Data
public class DeptDoctorOverviewDTO {

    /** 科室信息 */
    private DeptDetailDTO dept;

    /** 该科室下所有医生（包含今日号源情况） */
    private List<DoctorSlotDTO> doctors;
}
