// src/main/java/com/hospital/dto/NurseTriageRequest.java
package com.hospital.dto;

public class NurseTriageRequest {

    // 当前登录护士的 userId（users.user_id）
    private Long nurseId;

    // 分诊备注
    private String triageNote;

    public Long getNurseId() { return nurseId; }
    public void setNurseId(Long nurseId) { this.nurseId = nurseId; }

    public String getTriageNote() { return triageNote; }
    public void setTriageNote(String triageNote) { this.triageNote = triageNote; }
}
