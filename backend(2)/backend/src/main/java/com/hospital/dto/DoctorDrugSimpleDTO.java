package com.hospital.dto;

public class DoctorDrugSimpleDTO {

    private Long drugId;
    private String drugName;
    private String spec;
    private String dosageUsage;

    public Long getDrugId() { return drugId; }
    public void setDrugId(Long drugId) { this.drugId = drugId; }
    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }
    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }
    public String getDosageUsage() { return dosageUsage; }
    public void setDosageUsage(String dosageUsage) { this.dosageUsage = dosageUsage; }
}
