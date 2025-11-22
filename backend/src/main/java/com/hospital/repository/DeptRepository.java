package com.hospital.repository;

import com.hospital.model.Dept;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeptRepository extends JpaRepository<Dept, Long> {

    /**
     * 根据科室编码查询科室
     * 注意：这里的 code 要和 Dept 实体里的字段名一致：private String code;
     */
    Optional<Dept> findByCode(String code);
}
