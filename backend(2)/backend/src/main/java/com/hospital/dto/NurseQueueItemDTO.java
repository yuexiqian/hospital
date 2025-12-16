// src/main/java/com/hospital/dto/NurseQueueItemDTO.java
package com.hospital.dto;

import java.time.LocalDateTime;

public class NurseQueueItemDTO {

    private Long id;
    private Long patientId;
    private String patientName;   // ✅ 新增：患者姓名

    private Long deptId;
    private String deptName;
    private Long doctorId;
    private String doctorName;

    private LocalDateTime registerTime;

    private Integer queueStatus;
    private Integer queuePriority;
    private Integer calledTimes;
    private LocalDateTime lastCallTime;

    private Long triageNurseId;
    private LocalDateTime triageTime;
    private String triageNote;

    // ===== getter / setter =====

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

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public LocalDateTime getRegisterTime() { return registerTime; }
    public void setRegisterTime(LocalDateTime registerTime) { this.registerTime = registerTime; }

    public Integer getQueueStatus() { return queueStatus; }
    public void setQueueStatus(Integer queueStatus) { this.queueStatus = queueStatus; }

    public Integer getQueuePriority() { return queuePriority; }
    public void setQueuePriority(Integer queuePriority) { this.queuePriority = queuePriority; }

    public Integer getCalledTimes() { return calledTimes; }
    public void setCalledTimes(Integer calledTimes) { this.calledTimes = calledTimes; }

    public LocalDateTime getLastCallTime() { return lastCallTime; }
    public void setLastCallTime(LocalDateTime lastCallTime) { this.lastCallTime = lastCallTime; }

    public Long getTriageNurseId() { return triageNurseId; }
    public void setTriageNurseId(Long triageNurseId) { this.triageNurseId = triageNurseId; }

    public LocalDateTime getTriageTime() { return triageTime; }
    public void setTriageTime(LocalDateTime triageTime) { this.triageTime = triageTime; }

    public String getTriageNote() { return triageNote; }
    public void setTriageNote(String triageNote) { this.triageNote = triageNote; }
}
