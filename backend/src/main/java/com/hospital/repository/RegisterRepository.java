package com.hospital.repository;

import com.hospital.model.RegisterRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RegisterRepository extends JpaRepository<RegisterRecord, Long> {

    /**
     * 查某用户在某时间段的所有挂号记录（按时间倒序）
     */
    List<RegisterRecord> findByUserIdAndRegisterTimeBetweenOrderByRegisterTimeDesc(
            Long userId, LocalDateTime start, LocalDateTime end
    );

    /**
     * 某个医生某一天内 queueNo 的最大值，用来生成下一位的排队号
     */
    List<RegisterRecord> findByDoctorIdAndRegisterTimeBetweenOrderByQueueNoDesc(
            Long doctorId, LocalDateTime start, LocalDateTime end
    );

    /**
     * 统计：同一个医生、同一天、处于 WAITING 状态、排队号小于当前号的数量
     * 用来算 aheadCount
     */
    long countByDoctorIdAndRegisterTimeBetweenAndStatusAndQueueNoLessThan(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end,
            String status,
            Integer queueNo
    );
    // 按科室统计今日 WAITING 数量
    long countByDeptIdAndRegisterTimeBetweenAndStatus(
            Long deptId, LocalDateTime start, LocalDateTime end, String status);

    // 按医生统计今日 WAITING 数量（用于医生列表）
    long countByDoctorIdAndRegisterTimeBetweenAndStatus(
            Long doctorId, LocalDateTime start, LocalDateTime end, String status);
}
