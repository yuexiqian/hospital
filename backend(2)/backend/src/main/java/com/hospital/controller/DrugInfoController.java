package com.hospital.controller;

import com.hospital.dto.DrugInfoDTO;
import com.hospital.model.DrugInfo;
import com.hospital.repository.DrugInfoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drugs")
@CrossOrigin
public class DrugInfoController {

    private final DrugInfoRepository repository;

    public DrugInfoController(DrugInfoRepository repository) {
        this.repository = repository;
    }

    /**
     * 1) 列表 + 搜索
     * GET /api/drugs
     * GET /api/drugs?keyword=氨溴索
     * GET /api/drugs?category=呼吸系统
     */
    @GetMapping
    public List<DrugInfoDTO> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category
    ) {
        List<DrugInfo> list;

        if (keyword != null && !keyword.isBlank()) {
            list = repository.searchByKeyword(keyword);
        } else if (category != null && !category.isBlank()) {
            list = repository.findByCategory(category);
        } else {
            list = repository.findAll();
        }

        return list.stream().map(this::toDTO).toList();
    }

    /**
     * 2) 单条详情
     * GET /api/drugs/{id}
     */
    @GetMapping("/{id}")
    public DrugInfoDTO getOne(@PathVariable Long id) {
        DrugInfo info = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("药品不存在"));
        return toDTO(info);
    }

    // === 可选：新增一条药品（方便你用 Postman 录入几条测试数据） ===
    @PostMapping
    public DrugInfoDTO create(@RequestBody DrugInfoDTO dto) {
        DrugInfo entity = new DrugInfo();
        BeanUtils.copyProperties(dto, entity);
        // 如果你在实体里加了 createTime / updateTime，可以这里顺手设置
        entity = repository.save(entity);
        return toDTO(entity);
    }

    // =================== 内部工具方法 ===================
    private DrugInfoDTO toDTO(DrugInfo d) {
        DrugInfoDTO dto = new DrugInfoDTO();
        BeanUtils.copyProperties(d, dto);
        return dto;
    }
}
