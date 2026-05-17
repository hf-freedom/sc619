package com.procurement.service;

import com.procurement.model.PurchaseRequest;
import com.procurement.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseRequestService {
    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Value("${procurement.comparison-threshold:10000}")
    private java.math.BigDecimal comparisonThreshold;

    public List<PurchaseRequest> getAllPurchaseRequests() {
        return new ArrayList<>(storage.purchaseRequests.values());
    }

    public PurchaseRequest getPurchaseRequestById(String id) {
        return storage.purchaseRequests.get(id);
    }

    public PurchaseRequest createPurchaseRequest(PurchaseRequest request) {
        request.setId(UUID.randomUUID().toString());
        request.setStatus(PurchaseRequest.Status.DRAFT);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        request.setRequiresComparison(false);
        storage.purchaseRequests.put(request.getId(), request);
        return request;
    }

    public PurchaseRequest submitPurchaseRequest(String id) {
        PurchaseRequest request = storage.purchaseRequests.get(id);
        if (request == null) return null;

        if (!budgetService.checkBudgetSufficient(request.getBudgetId(), request.getEstimatedAmount())) {
            request.setStatus(PurchaseRequest.Status.BUDGET_CHECK_FAILED);
            request.setUpdatedAt(LocalDateTime.now());
            return request;
        }

        budgetService.freezeBudget(request.getBudgetId(), request.getEstimatedAmount());

        request.setRequiresComparison(request.getEstimatedAmount().compareTo(comparisonThreshold) >= 0);
        
        if (request.getRequiresComparison()) {
            request.setStatus(PurchaseRequest.Status.PENDING_QUOTATION);
        } else {
            request.setStatus(PurchaseRequest.Status.PENDING_APPROVAL);
        }
        
        request.setUpdatedAt(LocalDateTime.now());
        return request;
    }

    public PurchaseRequest approvePurchaseRequest(String id) {
        PurchaseRequest request = storage.purchaseRequests.get(id);
        if (request == null) return null;

        request.setStatus(PurchaseRequest.Status.APPROVED);
        request.setUpdatedAt(LocalDateTime.now());

        if (!request.getRequiresComparison()) {
            purchaseOrderService.createPurchaseOrderDirect(id);
        }

        return request;
    }

    public PurchaseRequest rejectPurchaseRequest(String id) {
        PurchaseRequest request = storage.purchaseRequests.get(id);
        if (request == null) return null;

        budgetService.unfreezeBudget(request.getBudgetId(), request.getEstimatedAmount());

        request.setStatus(PurchaseRequest.Status.REJECTED);
        request.setUpdatedAt(LocalDateTime.now());
        return request;
    }

    public PurchaseRequest cancelPurchaseRequest(String id) {
        PurchaseRequest request = storage.purchaseRequests.get(id);
        if (request == null) return null;

        budgetService.unfreezeBudget(request.getBudgetId(), request.getEstimatedAmount());

        storage.supplierQuotes.values().stream()
                .filter(q -> q.getPurchaseRequestId().equals(id))
                .forEach(q -> q.setStatus(com.procurement.model.SupplierQuote.Status.CANCELLED));

        request.setStatus(PurchaseRequest.Status.CANCELLED);
        request.setUpdatedAt(LocalDateTime.now());
        return request;
    }

    public void updateStatus(String id, PurchaseRequest.Status status) {
        PurchaseRequest request = storage.purchaseRequests.get(id);
        if (request != null) {
            request.setStatus(status);
            request.setUpdatedAt(LocalDateTime.now());
        }
    }
}
