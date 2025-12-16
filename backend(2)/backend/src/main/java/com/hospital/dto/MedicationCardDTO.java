package com.hospital.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 首页 A 区：最近用药指导卡片 DTO
 */
@Data
public class MedicationCardDTO {

    /** 是否存在用药指导记录 */
    private boolean hasGuide;

    /** 用药指导ID（medication_guide.id） */
    private Long guideId;

    /** 就诊人ID（可选，方便以后扩展） */
    private Long patientId;

    /** 科室名称（medication_guide.dept_name） */
    private String deptName;

    /** 医生名称（medication_guide.doctor_name） */
    private String doctorName;

    /** 指导标题（medication_guide.title） */
    private String title;

    /**
     * 内容预览：
     * 目前先直接用 medication_guide.content，
     * 以后如果你想只截取前 50 字再展示，也可以在 Service 里做处理。
     */
    private String contentPreview;

    /** 是否已查看（medication_guide.viewed） */
    private Boolean viewed;

    /** 创建时间（medication_guide.create_time） */
    private LocalDateTime createTime;
}
