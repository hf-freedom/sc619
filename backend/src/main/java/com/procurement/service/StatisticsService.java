package com.procurement.service;

import com.procurement.model.*;
import com.procurement.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StatisticsService {
    @Autowired
    private InMemoryStorage storage;

    public ProcurementStatistics getStatistics() {
        ProcurementStatistics stats = new ProcurementStatistics();
        
        BigDecimal totalSaved = BigDecimal.ZERO;
        for (PurchaseOrder order : storage.purchaseOrders.values()) {
            PurchaseRequest request = storage.purchaseRequests.get(order.getPurchaseRequestId());
            if (request != null) {
                totalSaved = totalSaved.add(request.getEstimatedAmount().subtract(order.getTotalAmount()));
            }
        }
        stats.setTotalSavedAmount(totalSaved);
        
        int totalDelays = storage.suppliers.values().stream()
                .mapToInt(Supplier::getDelayCount)
                .sum();
        stats.setTotalDelayCount(totalDelays);
        
        double avgRating = storage.suppliers.values().stream()
                .mapToDouble(s -> s.getRating().doubleValue())
                .average()
                .orElse(0.0);
        stats.setAverageSupplierRating(avgRating);
        
        stats.setTotalPurchaseOrders(storage.purchaseOrders.size());
        
        long completed = storage.purchaseOrders.values().stream()
                .filter(o -> o.getFulfillmentStatus() == PurchaseOrder.FulfillmentStatus.FULLY_DELIVERED)
                .count();
        stats.setCompletedOrders((int) completed);
        
        return stats;
    }
}
