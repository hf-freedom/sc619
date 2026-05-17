package com.procurement.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PurchaseRequest {
    private String id;
    private String title;
    private String description;
    private String requesterId;
    private String requesterName;
    private String budgetId;
    private BigDecimal estimatedAmount;
    private Integer deliveryDays;
    private Status status;
    private Boolean requiresComparison;
    private String selectedSupplierId;
    private List<String> approverIds = new ArrayList<>();
    private Integer currentApprovalIndex = 0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        DRAFT,
        PENDING_APPROVAL,
        BUDGET_CHECK_FAILED,
        PENDING_QUOTATION,
        QUOTATION_COMPLETED,
        APPROVED,
        REJECTED,
        CANCELLED,
        ORDER_CREATED
    }
}
