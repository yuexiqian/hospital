package com.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PatientRequest {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String gender;

    @NotBlank
    private String certificateType;

    @NotBlank
    private String certificateNo;

    @NotBlank
    private String relationship;

    @NotBlank
    private String contactPhone;
}

