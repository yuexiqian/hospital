package com.hospital.dto;

import java.math.BigDecimal;
import java.util.List;

public class DoctorVisitSaveRequest {

    private String diag;
    private String remark;
    private boolean submitPrescription;
    private List<PrescriptionDTO> prescriptions;

    public String getDiag() { return diag; }
    public void setDiag(String diag) { this.diag = diag; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public boolean isSubmitPrescription() { return submitPrescription; }
    public void setSubmitPrescription(boolean submitPrescription) { this.submitPrescription = submitPrescription; }
    public List<PrescriptionDTO> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(List<PrescriptionDTO> prescriptions) { this.prescriptions = prescriptions; }

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
        private Integer quantity;
        private String dosage;
        private String frequency;
        private Integer days;
        private String remark;
        private BigDecimal amount;

        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public Long getDrugId() { return drugId; }
        public void setDrugId(Long drugId) { this.drugId = drugId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }
        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }
        public Integer getDays() { return days; }
        public void setDays(Integer days) { this.days = days; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
}
