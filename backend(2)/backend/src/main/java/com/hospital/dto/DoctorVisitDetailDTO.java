package com.hospital.dto;

import java.util.List;

public class DoctorVisitDetailDTO {

    private Long registerId;
    private String registerDate;
    private String registerTime;
    private String visitStatus;

    private Long patientId;
    private String patientName;
    private String idCard;
    private String phone;

    private Long deptId;
    private String deptName;

    private Long doctorId;
    private String doctorName;
    private String doctorTitle;

    private String diag;
    private String remark;

    private List<PrescriptionDTO> prescriptions;

    // --- getters/setters ---
    // 省略重复 setter/getter（照 IDE 一键生成即可）

    public Long getRegisterId() { return registerId; }
    public void setRegisterId(Long registerId) { this.registerId = registerId; }
    public String getRegisterDate() { return registerDate; }
    public void setRegisterDate(String registerDate) { this.registerDate = registerDate; }
    public String getRegisterTime() { return registerTime; }
    public void setRegisterTime(String registerTime) { this.registerTime = registerTime; }
    public String getVisitStatus() { return visitStatus; }
    public void setVisitStatus(String visitStatus) { this.visitStatus = visitStatus; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorTitle() { return doctorTitle; }
    public void setDoctorTitle(String doctorTitle) { this.doctorTitle = doctorTitle; }
    public String getDiag() { return diag; }
    public void setDiag(String diag) { this.diag = diag; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public List<PrescriptionDTO> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(List<PrescriptionDTO> prescriptions) { this.prescriptions = prescriptions; }

    // --- 内部处方 DTO ---

    public static class PrescriptionDTO {
        private Long prescriptionId;
        private String status;
        private List<PrescriptionItemDTO> items;

        public Long getPrescriptionId() { return prescriptionId; }
        public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public List<PrescriptionItemDTO> getItems() { return items; }
        public void setItems(List<PrescriptionItemDTO> items) { this.items = items; }
    }

    public static class PrescriptionItemDTO {
        private Long itemId;
        private Long drugId;
        private String drugName;
        private String dosage;
        private String frequency;
        private Integer days;
        private Integer quantity;
        private String remark;

        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public Long getDrugId() { return drugId; }
        public void setDrugId(Long drugId) { this.drugId = drugId; }
        public String getDrugName() { return drugName; }
        public void setDrugName(String drugName) { this.drugName = drugName; }
        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }
        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }
        public Integer getDays() { return days; }
        public void setDays(Integer days) { this.days = days; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }
}
