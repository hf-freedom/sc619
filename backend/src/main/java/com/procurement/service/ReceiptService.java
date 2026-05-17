package com.procurement.service;

import com.procurement.model.PurchaseOrder;
import com.procurement.model.Receipt;
import com.procurement.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReceiptService {
    @Autowired
    private InMemoryStorage storage;

    public List<Receipt> getReceiptsByOrder(String purchaseOrderId) {
        return storage.receipts.values().stream()
                .filter(r -> r.getPurchaseOrderId().equals(purchaseOrderId))
                .collect(Collectors.toList());
    }

    public Receipt createReceipt(Receipt receipt) {
        receipt.setId(UUID.randomUUID().toString());
        receipt.setReceivedAt(LocalDateTime.now());
        receipt.setCreatedAt(LocalDateTime.now());
        storage.receipts.put(receipt.getId(), receipt);

        PurchaseOrder order = storage.purchaseOrders.get(receipt.getPurchaseOrderId());
        if (order != null) {
            order.setActualDeliveryDate(LocalDateTime.now());
            order.setFulfillmentStatus(PurchaseOrder.FulfillmentStatus.FULLY_DELIVERED);
            order.setUpdatedAt(LocalDateTime.now());
        }

        return receipt;
    }
}
