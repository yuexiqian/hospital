package com.hospital.repository;

import com.hospital.model.RegisterRecord;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface RegisterRepository
        extends JpaRepository<RegisterRecord, Long>, JpaSpecificationExecutor<RegisterRecord> {

    // ========= RegisterService 用到 =========

    List<RegisterRecord> findByDoctorIdAndRegisterTimeBetweenOrderByQueueNoDesc(
            Long doctorId, LocalDateTime start, LocalDateTime end
    );

    long countByDoctorIdAndRegisterTimeBetweenAndStatusAndQueueNoLessThan(
            Long doctorId, LocalDateTime start, LocalDateTime end, String status, Integer queueNo
    );

    List<RegisterRecord> findByUserIdAndRegisterTimeBetweenOrderByRegisterTimeDesc(
            Long userId, LocalDateTime start, LocalDateTime end
    );

    List<RegisterRecord> findByUserIdAndPatientIdAndRegisterTimeBetweenOrderByRegisterTimeDesc(
            Long userId, Long patientId, LocalDateTime start, LocalDateTime end
    );

    // ========= DoctorServiceImpl =========

    List<RegisterRecord> findByDoctorIdAndRegisterTimeBetweenOrderByRegisterTimeAsc(
            Long doctorId, LocalDateTime start, LocalDateTime end
    );

    List<RegisterRecord> findByPatientIdOrderByRegisterTimeDesc(Long patientId);

    // ========= NurseQueueServiceImpl：队列列表 =========

    @Query("""
        select rr
        from RegisterRecord rr
        where rr.registerTime >= :start and rr.registerTime < :end
          and (:deptId is null or rr.deptId = :deptId)
          and (:doctorId is null or rr.doctorId = :doctorId)
          and (:queueStatus is null or rr.queueStatus = :queueStatus)
        order by coalesce(rr.queuePriority, 0) desc, rr.registerTime asc
    """)
    List<RegisterRecord> findQueueList(@Param("deptId") Long deptId,
                                       @Param("doctorId") Long doctorId,
                                       @Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("queueStatus") Integer queueStatus);

    // ========= TriageServiceImpl：候诊人数统计 =========

    long countByDoctorIdAndRegisterTimeBetweenAndStatus(
            Long doctorId, LocalDateTime start, LocalDateTime end, String status
    );

    long countByDeptIdAndRegisterTimeBetweenAndStatus(
            Long deptId, LocalDateTime start, LocalDateTime end, String status
    );

    // ✅ DeptServiceImpl：号源占用统计（status IN ...）
    long countByDoctorIdAndRegisterTimeBetweenAndStatusIn(
            Long doctorId, LocalDateTime start, LocalDateTime end, Collection<String> status
    );

    // ========= Dashboard/统计：native SQL =========

    interface QueueStatusCountProjection {
        Integer getQueueStatus();
        Long getCnt();
    }

    interface DeptCountProjection {
        Long getDeptId();
        String getDeptName();
        Long getCnt();
    }

    interface DayCountProjection {
        java.sql.Date getDay();
        Long getCnt();
    }

    @Query(value = """
        select rr.queue_status as queueStatus, count(*) as cnt
        from register_record rr
        where rr.register_time >= :start and rr.register_time < :end
        group by rr.queue_status
        order by rr.queue_status
        """, nativeQuery = true)
    List<QueueStatusCountProjection> countByQueueStatus(@Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end);

    @Query(value = """
        select rr.dept_id as deptId, rr.dept_name as deptName, count(*) as cnt
        from register_record rr
        where rr.register_time >= :start and rr.register_time < :end
        group by rr.dept_id, rr.dept_name
        order by cnt desc
        """, nativeQuery = true)
    List<DeptCountProjection> countByDept(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query(value = """
        select date(rr.register_time) as day, count(*) as cnt
        from register_record rr
        where rr.register_time >= :start and rr.register_time < :end
        group by date(rr.register_time)
        order by day asc
        """, nativeQuery = true)
    List<DayCountProjection> countByDay(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query(value = """
        select count(*)
        from register_record rr
        where rr.register_time >= :start and rr.register_time < :end
          and rr.triage_time is not null
        """, nativeQuery = true)
    long countTriaged(@Param("start") LocalDateTime start,
                      @Param("end") LocalDateTime end);
}
