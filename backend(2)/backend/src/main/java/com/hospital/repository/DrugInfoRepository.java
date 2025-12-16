// src/main/java/com/hospital/repository/DrugInfoRepository.java
package com.hospital.repository;

import com.hospital.model.DrugInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DrugInfoRepository extends JpaRepository<DrugInfo, Long> {

    // 按分类查
    List<DrugInfo> findByCategory(String category);

    /**
     * 关键字搜索：在 药品名 / 通用名 / 英文名 / 适应症 里模糊匹配
     */
    @Query("""
        select d from DrugInfo d
        where d.drugName    like concat('%', :keyword, '%')
           or d.genericName like concat('%', :keyword, '%')
           or d.englishName like concat('%', :keyword, '%')
           or d.indications like concat('%', :keyword, '%')
    """)
    List<DrugInfo> searchByKeyword(@Param("keyword") String keyword);
}
