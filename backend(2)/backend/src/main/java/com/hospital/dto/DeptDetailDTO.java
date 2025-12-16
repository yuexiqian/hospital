package com.hospital.dto;

import lombok.Data;

/**
 * 科室详情 DTO：给首页“科室介绍”用
 */
@Data
public class DeptDetailDTO {

    private Long id;

    private String name;

    private String type;

    /** 楼层（保留 String，更灵活） */
    private String floor;

    /** 科室简介 */
    private String description;

    /** 门诊位置 */
    private String location;

    /** 门诊时间 */
    private String openingHours;

    /** 擅长疾病/特色 */
    private String specialty;
}
