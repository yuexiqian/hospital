// src/main/java/com/hospital/service/NurseQueueService.java
package com.hospital.service;

import com.hospital.dto.NurseQueueItemDTO;
import com.hospital.dto.NurseTriageRequest;

import java.time.LocalDate;
import java.util.List;

public interface NurseQueueService {

    // 查询候诊队列
    List<NurseQueueItemDTO> listQueue(Long deptId,
                                      Long doctorId,
                                      LocalDate visitDate,
                                      Integer queueStatus);

    // 叫号
    void callPatient(Long registerId, Long nurseId);

    // 标记过号
    void markNoShow(Long registerId);

    // 设置加急
    void setPriority(Long registerId, Integer priority);

    // 取消加急
    void cancelPriority(Long registerId);

    // 保存分诊信息（备注 + 分诊护士）
    void saveTriageInfo(Long registerId, NurseTriageRequest req);
}
