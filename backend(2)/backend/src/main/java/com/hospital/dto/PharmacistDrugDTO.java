package com.hospital.dto;

import java.math.BigDecimal;

/**
 * 药师端药品列表 DTO
 */
public class PharmacistDrugDTO {

    private Long id;
    private String drugCode;     // 药品编码
    private String drugName;     // 商品名 / 药品名
    private String genericName;  // 通用名
    private String spec;         // 规格
    private String dosageForm;   // 剂型
    private String unit;         // 单位
    private BigDecimal unitPrice;
    private Integer isActive;    // 1 启用 0 停用
    private String category;     // 分类，可选

    // ===== getter / setter =====

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDrugCode() { return drugCode; }

    public void setDrugCode(String drugCode) { this.drugCode = drugCode; }

    public String getDrugName() { return drugName; }

    public void setDrugName(String drugName) { this.drugName = drugName; }

    public String getGenericName() { return genericName; }

    public void setGenericName(String genericName) { this.genericName = genericName; }

    public String getSpec() { return spec; }

    public void setSpec(String spec) { this.spec = spec; }

    public String getDosageForm() { return dosageForm; }

    public void setDosageForm(String dosageForm) { this.dosageForm = dosageForm; }

    public String getUnit() { return unit; }

    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getUnitPrice() { return unitPrice; }

    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getIsActive() { return isActive; }

    public void setIsActive(Integer isActive) { this.isActive = isActive; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }
}
