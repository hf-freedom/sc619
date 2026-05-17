package com.procurement.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PurchaseOrder {
    private String id;
    private String purchaseRequestId;
    private String supplierId;
    private String supplierName;
    private BigDecimal totalAmount;
    private Integer deliveryDays;
    private LocalDateTime expectedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private FulfillmentStatus fulfillmentStatus;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum FulfillmentStatus {
        PENDING_DELIVERY,
        PARTIALLY_DELIVERED,
        FULLY_DELIVERED,
        DELAYED,
        CANCELLED
    }
}
