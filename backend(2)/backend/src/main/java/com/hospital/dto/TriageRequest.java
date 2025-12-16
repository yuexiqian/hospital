package com.hospital.dto;

import lombok.Data;

import java.util.List;

@Data
public class TriageRequest {

    /**
     * 不适部位：胸部/腹部/头部/四肢/皮肤/其他...
     */
    private String bodyPart;

    /**
     * 症状列表：咳嗽、发热、胸闷...
     */
    private List<String> symptoms;

    /**
     * 程度：轻度/中度/重度
     */
    private String severity;

    /**
     * 持续时间：<24h、1-3天、>3天
     */
    private String duration;

    /**
     * 是否紧急：true/false
     */
    private Boolean emergency;

    /**
     * 其他补充描述（选填）
     */
    private String extraDesc;
}
