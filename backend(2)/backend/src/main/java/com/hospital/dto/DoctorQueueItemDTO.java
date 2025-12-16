package com.hospital.dto;

public class DoctorQueueItemDTO {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long deptId;
    private String deptName;
    private String registerTime;  // HH:mm
    private Integer queueStatus;

    // getters & setters
    // ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getRegisterTime() { return registerTime; }
    public void setRegisterTime(String registerTime) { this.registerTime = registerTime; }

    public Integer getQueueStatus() { return queueStatus; }
    public void setQueueStatus(Integer queueStatus) { this.queueStatus = queueStatus; }
}
