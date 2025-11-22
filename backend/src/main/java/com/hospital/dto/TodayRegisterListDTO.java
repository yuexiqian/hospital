package com.hospital.dto;

import lombok.Data;

import java.util.List;

@Data
public class TodayRegisterListDTO {

    private long count;

    private List<RegisterInfoDTO> items;
}
