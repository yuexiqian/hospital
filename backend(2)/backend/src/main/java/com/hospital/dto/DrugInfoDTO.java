package com.hospital.dto;

import lombok.Data;

/**
 * 药品知识库 DTO
 */
@Data
public class DrugInfoDTO {
    private Long id;
    private String drugName;
    private String genericName;
    private String englishName;
    private String category;
    private String dosageForm;
    private String spec;
    private String indications;
    private String dosageUsage;
    private String adverseReaction;
    private String contraindication;
    private String precautions;
    private String interactions;
    private String storage;
    private String reference;
}
