package com.procurement.controller;

import com.procurement.model.PurchaseOrder;
import com.procurement.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getAllPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderById(@PathVariable String id) {
        PurchaseOrder order = purchaseOrderService.getPurchaseOrderById(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@RequestBody Map<String, String> payload) {
        String purchaseRequestId = payload.get("purchaseRequestId");
        String selectedQuoteId = payload.get("selectedQuoteId");
        PurchaseOrder order = purchaseOrderService.createPurchaseOrderFromQuote(purchaseRequestId, selectedQuoteId);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.badRequest().build();
    }

    @PostMapping("/{id}/confirm-delivery")
    public ResponseEntity<PurchaseOrder> confirmDelivery(@PathVariable String id) {
        PurchaseOrder order = purchaseOrderService.confirmDelivery(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PurchaseOrder> cancelPurchaseOrder(@PathVariable String id) {
        PurchaseOrder order = purchaseOrderService.cancelPurchaseOrder(id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }
}
