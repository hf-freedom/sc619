package com.procurement.service;

import com.procurement.model.*;
import com.procurement.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderService {
    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private SupplierQuoteService quoteService;

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return new ArrayList<>(storage.purchaseOrders.values());
    }

    public PurchaseOrder getPurchaseOrderById(String id) {
        return storage.purchaseOrders.get(id);
    }

    public PurchaseOrder createPurchaseOrderFromQuote(String purchaseRequestId, String selectedQuoteId) {
        PurchaseRequest request = storage.purchaseRequests.get(purchaseRequestId);
        SupplierQuote quote = storage.supplierQuotes.get(selectedQuoteId);
        
        if (request == null || quote == null) return null;

        PurchaseOrder order = new PurchaseOrder();
        order.setId(UUID.randomUUID().toString());
        order.setPurchaseRequestId(purchaseRequestId);
        order.setSupplierId(quote.getSupplierId());
        order.setSupplierName(quote.getSupplierName());
        order.setTotalAmount(quote.getQuotedPrice());
        order.setDeliveryDays(quote.getDeliveryDays());
        order.setExpectedDeliveryDate(LocalDateTime.now().plusDays(quote.getDeliveryDays()));
        order.setFulfillmentStatus(PurchaseOrder.FulfillmentStatus.PENDING_DELIVERY);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        storage.purchaseOrders.put(order.getId(), order);

        budgetService.unfreezeBudget(request.getBudgetId(), request.getEstimatedAmount());
        budgetService.useBudget(request.getBudgetId(), quote.getQuotedPrice());

        supplierService.incrementOrderCount(quote.getSupplierId());

        request.setStatus(PurchaseRequest.Status.ORDER_CREATED);
        request.setSelectedSupplierId(quote.getSupplierId());
        request.setUpdatedAt(LocalDateTime.now());

        createReceiptTask(order.getId());

        return order;
    }

    public PurchaseOrder createPurchaseOrderDirect(String purchaseRequestId) {
        PurchaseRequest request = storage.purchaseRequests.get(purchaseRequestId);
        if (request == null) return null;

        List<Supplier> suppliers = supplierService.getAllSuppliers();
        Supplier defaultSupplier = suppliers.isEmpty() ? null : suppliers.get(0);

        PurchaseOrder order = new PurchaseOrder();
        order.setId(UUID.randomUUID().toString());
        order.setPurchaseRequestId(purchaseRequestId);
        if (defaultSupplier != null) {
            order.setSupplierId(defaultSupplier.getId());
            order.setSupplierName(defaultSupplier.getName());
        } else {
            order.setSupplierName("默认供应商");
        }
        order.setTotalAmount(request.getEstimatedAmount());
        order.setDeliveryDays(request.getDeliveryDays() != null ? request.getDeliveryDays() : 7);
        order.setExpectedDeliveryDate(LocalDateTime.now().plusDays(order.getDeliveryDays()));
        order.setFulfillmentStatus(PurchaseOrder.FulfillmentStatus.PENDING_DELIVERY);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        storage.purchaseOrders.put(order.getId(), order);

        budgetService.unfreezeBudget(request.getBudgetId(), request.getEstimatedAmount());
        budgetService.useBudget(request.getBudgetId(), request.getEstimatedAmount());

        if (defaultSupplier != null) {
            supplierService.incrementOrderCount(defaultSupplier.getId());
        }

        request.setStatus(PurchaseRequest.Status.ORDER_CREATED);
        request.setSelectedSupplierId(order.getSupplierId());
        request.setUpdatedAt(LocalDateTime.now());

        createReceiptTask(order.getId());

        return order;
    }

    private void createReceiptTask(String orderId) {
        Receipt receipt = new Receipt();
        receipt.setId(UUID.randomUUID().toString());
        receipt.setPurchaseOrderId(orderId);
        receipt.setCreatedAt(LocalDateTime.now());
        storage.receipts.put(receipt.getId(), receipt);
    }

    public PurchaseOrder confirmDelivery(String orderId) {
        PurchaseOrder order = storage.purchaseOrders.get(orderId);
        if (order == null) return null;

        order.setActualDeliveryDate(LocalDateTime.now());
        order.setFulfillmentStatus(PurchaseOrder.FulfillmentStatus.FULLY_DELIVERED);
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public PurchaseOrder cancelPurchaseOrder(String orderId) {
        PurchaseOrder order = storage.purchaseOrders.get(orderId);
        if (order == null) return null;

        PurchaseRequest request = storage.purchaseRequests.get(order.getPurchaseRequestId());
        if (request != null) {
            budgetService.releaseUsedBudget(request.getBudgetId(), order.getTotalAmount());
        }

        order.setFulfillmentStatus(PurchaseOrder.FulfillmentStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }
}
