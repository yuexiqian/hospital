package com.hospital.service;

import java.math.BigDecimal;

public interface BillService {

    long countUnpaidByUser(Long userId);

    BigDecimal sumUnpaidAmountByUser(Long userId);
}
