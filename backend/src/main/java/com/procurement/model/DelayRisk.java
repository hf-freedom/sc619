package com.procurement.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DelayRisk {
    private String id;
    private String purchaseOrderId;
    private String supplierId;
    private String supplierName;
    private RiskLevel riskLevel;
    private Integer expectedDelayDays;
    private String description;
    private Boolean resolved;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
