package com.procurement.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProcurementStatistics {
    private BigDecimal totalSavedAmount;
    private Integer totalDelayCount;
    private Double averageSupplierRating;
    private Integer totalPurchaseOrders;
    private Integer completedOrders;
}
