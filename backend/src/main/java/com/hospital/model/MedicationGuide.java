package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "medication_guide")
public class MedicationGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录用户 id（users 表的 id） */
    private Long userId;

    /** 就诊人 id，可选 */
    private Long patientId;

    /** 科室名称 */
    private String deptName;

    /** 医生名称 */
    private String doctorName;

    /** 简短标题 */
    private String title;

    /** 详细用药内容 */
    @Lob
    private String content;

    /** 是否已查看 */
    private Boolean viewed = false;

    /** 创建时间 */
    private LocalDateTime createTime = LocalDateTime.now();
}
