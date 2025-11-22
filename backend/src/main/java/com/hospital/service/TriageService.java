package com.hospital.service;

import com.hospital.dto.TriageRequest;
import com.hospital.dto.TriageResultDTO;

public interface TriageService {

    /**
     * 根据用户填写的分诊信息给出推荐科室、候诊情况和医生列表
     */
    TriageResultDTO advise(TriageRequest request);
}
