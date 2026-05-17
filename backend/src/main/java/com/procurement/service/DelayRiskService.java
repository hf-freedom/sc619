package com.procurement.service;

import com.procurement.model.DelayRisk;
import com.procurement.model.PurchaseOrder;
import com.procurement.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DelayRiskService {
    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private SupplierService supplierService;

    public List<DelayRisk> getAllDelayRisks() {
        return new ArrayList<>(storage.delayRisks.values());
    }

    @Scheduled(fixedRate = 60000)
    public void checkDelayedOrders() {
        LocalDateTime now = LocalDateTime.now();
        
        storage.purchaseOrders.values().stream()
                .filter(order -> order.getFulfillmentStatus() == PurchaseOrder.FulfillmentStatus.PENDING_DELIVERY)
                .filter(order -> order.getExpectedDeliveryDate() != null && now.isAfter(order.getExpectedDeliveryDate()))
                .forEach(order -> {
                    long delayDays = ChronoUnit.DAYS.between(order.getExpectedDeliveryDate(), now);
                    
                    boolean existingRisk = storage.delayRisks.values().stream()
                            .anyMatch(r -> r.getPurchaseOrderId().equals(order.getId()) && !r.getResolved());
                    
                    if (!existingRisk) {
                        DelayRisk risk = new DelayRisk();
                        risk.setId(UUID.randomUUID().toString());
                        risk.setPurchaseOrderId(order.getId());
                        risk.setSupplierId(order.getSupplierId());
                        risk.setSupplierName(order.getSupplierName());
                        risk.setExpectedDelayDays((int) delayDays);
                        risk.setDescription("订单已逾期 " + delayDays + " 天");
                        risk.setResolved(false);
                        risk.setCreatedAt(now);
                        
                        if (delayDays >= 30) {
                            risk.setRiskLevel(DelayRisk.RiskLevel.CRITICAL);
                        } else if (delayDays >= 14) {
                            risk.setRiskLevel(DelayRisk.RiskLevel.HIGH);
                        } else if (delayDays >= 7) {
                            risk.setRiskLevel(DelayRisk.RiskLevel.MEDIUM);
                        } else {
                            risk.setRiskLevel(DelayRisk.RiskLevel.LOW);
                        }
                        
                        storage.delayRisks.put(risk.getId(), risk);
                        
                        order.setFulfillmentStatus(PurchaseOrder.FulfillmentStatus.DELAYED);
                        order.setUpdatedAt(now);
                        
                        supplierService.incrementDelayCount(order.getSupplierId());
                    }
                });
    }

    public DelayRisk resolveRisk(String riskId) {
        DelayRisk risk = storage.delayRisks.get(riskId);
        if (risk != null) {
            risk.setResolved(true);
            risk.setResolvedAt(LocalDateTime.now());
        }
        return risk;
    }
}
