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

    /** 楼层/位置 */
    @Column(length = 20)
    private String floor;

    /** 状态：1 启用，0 停用 */
    private Integer status;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
