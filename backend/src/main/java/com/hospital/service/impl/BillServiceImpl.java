package com.hospital.service.impl;

import com.hospital.repository.BillRepository;
import com.hospital.service.BillService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository repository;

    public BillServiceImpl(BillRepository repository) {
        this.repository = repository;
    }

    @Override
    public long countUnpaidByUser(Long userId) {
        return repository.countByUserIdAndStatus(userId, "UNPAID");
    }

    @Override
    public BigDecimal sumUnpaidAmountByUser(Long userId) {
        return repository.sumAmountByUserIdAndStatus(userId, "UNPAID");
    }
}
