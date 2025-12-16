package com.hospital.dto;

public class DoctorVisitHistoryItemDTO {

    private Long registerId;
    private String visitTime;
    private String deptName;
    private String doctorName;
    private String diag;

    public Long getRegisterId() { return registerId; }
    public void setRegisterId(Long registerId) { this.registerId = registerId; }
    public String getVisitTime() { return visitTime; }
    public void setVisitTime(String visitTime) { this.visitTime = visitTime; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDiag() { return diag; }
    public void setDiag(String diag) { this.diag = diag; }
}
