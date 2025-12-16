package com.hospital.dto;

import lombok.Data;

@Data
public class HomeOverviewDTO {

    private TodayRegisterCardDTO todayRegister;
    private QueueStatusDTO queueStatus;
    private MedicationCardDTO medication;
    private BillsSummaryDTO bills;
}
