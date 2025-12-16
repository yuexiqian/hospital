// src/main/java/com/hospital/dto/DispenseRequest.java
package com.hospital.dto;

import lombok.Data;

@Data
public class DispenseRequest {

    private Long pharmacistId;      // 药师 ID（当前登录用户 ID）
    private String pharmacistName;  // 药师姓名（当前登录用户 loginName）
    private String remark;          // 备注（可选）
}
