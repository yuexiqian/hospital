// src/main/java/com/hospital/service/impl/PharmacistServiceImpl.java
package com.hospital.service.impl;

import com.hospital.dto.DrugInfoDTO;
import com.hospital.dto.DrugInfoUpdateRequest;
import com.hospital.model.DrugInfo;
import com.hospital.repository.DrugInfoRepository;
import com.hospital.service.PharmacistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PharmacistServiceImpl implements PharmacistService {

    private final DrugInfoRepository drugInfoRepository;

    public PharmacistServiceImpl(DrugInfoRepository drugInfoRepository) {
        this.drugInfoRepository = drugInfoRepository;
    }

    /**
     * 药品列表 / 搜索
     */
    @Override
    public List<DrugInfoDTO> searchDrugInfos(String keyword) {
        List<DrugInfo> list;

        if (keyword == null || keyword.trim().isEmpty()) {
            // 关键字为空：查全部（后面可以再做分页）
            list = drugInfoRepository.findAll();
        } else {
            String kw = keyword.trim();
            list = drugInfoRepository.searchByKeyword(kw);
        }

        return list.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 单个药品详情
     */
    @Override
    public DrugInfoDTO getDrugInfo(Long id) {
        DrugInfo drug = drugInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("药品知识不存在，ID=" + id));
        return toDto(drug);
    }

    /**
     * 更新药品信息
     */
    @Override
    public void updateDrugInfo(Long id, DrugInfoUpdateRequest req) {
        DrugInfo drug = drugInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("药品知识不存在，ID=" + id));

        // 可编辑字段
        drug.setDrugName(req.getDrugName());
        drug.setGenericName(req.getGenericName());
        drug.setEnglishName(req.getEnglishName());
        drug.setCategory(req.getCategory());
        drug.setDosageForm(req.getDosageForm());
        drug.setSpec(req.getSpec());

        drug.setIndications(req.getIndications());
        drug.setDosageUsage(req.getDosageUsage());
        drug.setAdverseReaction(req.getAdverseReaction());
        drug.setContraindication(req.getContraindication());
        drug.setPrecautions(req.getPrecautions());
        drug.setInteractions(req.getInteractions());
        drug.setStorage(req.getStorage());
        drug.setReference(req.getReference());

        drug.setUpdateTime(LocalDateTime.now());

        drugInfoRepository.save(drug);
    }

    /**
     * ⭐ 新建药品信息
     */
    @Override
    public DrugInfoDTO createDrugInfo(DrugInfoUpdateRequest req) {
        DrugInfo drug = new DrugInfo();

        drug.setDrugName(req.getDrugName());
        drug.setGenericName(req.getGenericName());
        drug.setEnglishName(req.getEnglishName());
        drug.setCategory(req.getCategory());
        drug.setDosageForm(req.getDosageForm());
        drug.setSpec(req.getSpec());

        drug.setIndications(req.getIndications());
        drug.setDosageUsage(req.getDosageUsage());
        drug.setAdverseReaction(req.getAdverseReaction());
        drug.setContraindication(req.getContraindication());
        drug.setPrecautions(req.getPrecautions());
        drug.setInteractions(req.getInteractions());
        drug.setStorage(req.getStorage());
        drug.setReference(req.getReference());

        LocalDateTime now = LocalDateTime.now();
        drug.setCreateTime(now);
        drug.setUpdateTime(now);

        DrugInfo saved = drugInfoRepository.save(drug);
        return toDto(saved);
    }

    /**
     * 实体转 DTO
     */
    private DrugInfoDTO toDto(DrugInfo d) {
        DrugInfoDTO dto = new DrugInfoDTO();
        dto.setId(d.getId());
        dto.setDrugName(d.getDrugName());
        dto.setGenericName(d.getGenericName());
        dto.setEnglishName(d.getEnglishName());
        dto.setCategory(d.getCategory());
        dto.setDosageForm(d.getDosageForm());
        dto.setSpec(d.getSpec());
        dto.setIndications(d.getIndications());
        dto.setDosageUsage(d.getDosageUsage());
        dto.setAdverseReaction(d.getAdverseReaction());
        dto.setContraindication(d.getContraindication());
        dto.setPrecautions(d.getPrecautions());
        dto.setInteractions(d.getInteractions());
        dto.setStorage(d.getStorage());
        dto.setReference(d.getReference());
        return dto;
    }
}
