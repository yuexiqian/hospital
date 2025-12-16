// src/main/java/com/hospital/dto/UserVO.java
package com.hospital.dto;

public class UserVO {

    private Long userId;
    private String loginName;
    private String phone;
    private String role;

    // ✨ 新增：医生相关字段（只对 DOCTOR 有值，其它角色可以为 null）
    private Long doctorId;
    private String name;   // 医生姓名（以后护士/药师也可以复用这个字段）

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getLoginName() { return loginName; }
    public void setLoginName(String loginName) { this.loginName = loginName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
