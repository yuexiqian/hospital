package com.hospital.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 医生姓名 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 职称：主任医师、副主任、主治等 */
    @Column(length = 20)
    private String title;

    /** 所属科室ID */
    @Column(name = "dept_id", nullable = false)
    private Long deptId;

    /** 联系电话（可选） */
    @Column(length = 20)
    private String phone;

    /** 状态：1 在职，0 停用 */
    private Integer status;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
