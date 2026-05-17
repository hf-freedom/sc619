package com.procurement.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Receipt {
    private String id;
    private String purchaseOrderId;
    private String receiverId;
    private String receiverName;
    private BigDecimal receivedQuantity;
    private String remarks;
    private LocalDateTime receivedAt;
    private LocalDateTime createdAt;
}
