package com.hospital.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Lob;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 药品知识库实体
 */
@Data
@Entity
@Table(name = "drug_info")
public class DrugInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 商品名，如：盐酸氨溴索片 */
    private String drugName;

    /** 通用名 / 学名 */
    private String genericName;

    /** 英文名（可选） */
    private String englishName;

    /** 大类：呼吸系统/心血管/消化系统/抗感染 等 */
    private String category;

    /** 剂型：片剂、胶囊、注射剂等 */
    private String dosageForm;

    /** 规格：如 30mg*20片/盒 */
    private String spec;

    /** 适应症 */
    @Lob
    private String indications;

    /** 用法用量 */
    @Lob
    private String dosageUsage;

    /** 不良反应 */
    @Lob
    private String adverseReaction;

    /** 禁忌 */
    @Lob
    private String contraindication;

    /** 注意事项/特殊人群 */
    @Lob
    private String precautions;

    /** 药物相互作用 */
    @Lob
    private String interactions;

    /** 贮藏条件（可选） */
    @Lob
    private String storage;

    /** 参考来源：说明书版本/网站链接等 */
    private String reference;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
