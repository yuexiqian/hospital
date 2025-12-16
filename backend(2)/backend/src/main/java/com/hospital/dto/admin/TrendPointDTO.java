package com.hospital.dto.admin;

import lombok.Data;

@Data
public class TrendPointDTO {
    private String day; // yyyy-MM-dd
    private Long cnt;
}
