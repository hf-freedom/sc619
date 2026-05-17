package com.procurement.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SupplierQuote {
    private String id;
    private String purchaseRequestId;
    private String supplierId;
    private String supplierName;
    private BigDecimal quotedPrice;
    private Integer deliveryDays;
    private String remarks;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        SUBMITTED,
        SELECTED,
        REJECTED,
        CANCELLED
    }
}
