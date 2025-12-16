package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "dept")
public class Dept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 科室名称，例如：呼吸内科 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 科室编码，例如：NEU, PED */
    @Column(nullable = false, length = 20, unique = true)
    private String code;

    /** 类型：门诊/急诊/住院等，可选 */
    @Column(length = 20)
    private String type;

    /** 楼层/位置（简单描述即可，如 "2F"、"3F东区"） */
    @Column(length = 20)
    private String floor;

    /** 状态：1 启用，0 停用 */
    private Integer status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    /* =============== 升级版科室介绍字段 =============== */

    /** 科室简介：主要负责什么疾病，服务对象等 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 门诊位置：更详细的位置描述，如 "门诊楼2F-203" */
    @Column(length = 100)
    private String location;

    /** 门诊时间，如 "周一至周五 8:00-17:30" */
    @Column(name = "opening_hours", length = 100)
    private String openingHours;

    /** 科室擅长疾病/特色，如 "哮喘、慢性咳嗽、支气管炎等" */
    @Column(length = 255)
    private String specialty;

}
