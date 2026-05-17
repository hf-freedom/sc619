package com.procurement.controller;

import com.procurement.model.PurchaseRequest;
import com.procurement.service.PurchaseRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-requests")
public class PurchaseRequestController {
    @Autowired
    private PurchaseRequestService purchaseRequestService;

    @GetMapping
    public ResponseEntity<List<PurchaseRequest>> getAllPurchaseRequests() {
        return ResponseEntity.ok(purchaseRequestService.getAllPurchaseRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseRequest> getPurchaseRequestById(@PathVariable String id) {
        PurchaseRequest request = purchaseRequestService.getPurchaseRequestById(id);
        return request != null ? ResponseEntity.ok(request) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PurchaseRequest> createPurchaseRequest(@RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(purchaseRequestService.createPurchaseRequest(request));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<PurchaseRequest> submitPurchaseRequest(@PathVariable String id) {
        PurchaseRequest request = purchaseRequestService.submitPurchaseRequest(id);
        return request != null ? ResponseEntity.ok(request) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<PurchaseRequest> approvePurchaseRequest(@PathVariable String id) {
        PurchaseRequest request = purchaseRequestService.approvePurchaseRequest(id);
        return request != null ? ResponseEntity.ok(request) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<PurchaseRequest> rejectPurchaseRequest(@PathVariable String id) {
        PurchaseRequest request = purchaseRequestService.rejectPurchaseRequest(id);
        return request != null ? ResponseEntity.ok(request) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PurchaseRequest> cancelPurchaseRequest(@PathVariable String id) {
        PurchaseRequest request = purchaseRequestService.cancelPurchaseRequest(id);
        return request != null ? ResponseEntity.ok(request) : ResponseEntity.notFound().build();
    }
}
