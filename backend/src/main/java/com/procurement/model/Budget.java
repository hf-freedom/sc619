package com.procurement.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Budget {
    private String id;
    private String name;
    private String code;
    private BigDecimal totalAmount;
    private BigDecimal usedAmount;
    private BigDecimal frozenAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BigDecimal getAvailableAmount() {
        return totalAmount.subtract(usedAmount).subtract(frozenAmount);
    }
}
