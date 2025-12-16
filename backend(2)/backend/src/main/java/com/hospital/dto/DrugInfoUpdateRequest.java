// src/main/java/com/hospital/dto/DrugInfoUpdateRequest.java
package com.hospital.dto;

import lombok.Data;

/**
 * 药师编辑药品知识库时用的请求体
 */
@Data
public class DrugInfoUpdateRequest {

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
