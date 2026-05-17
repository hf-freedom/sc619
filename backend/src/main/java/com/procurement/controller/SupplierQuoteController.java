package com.procurement.controller;

import com.procurement.model.SupplierQuote;
import com.procurement.service.SupplierQuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotes")
public class SupplierQuoteController {
    @Autowired
    private SupplierQuoteService supplierQuoteService;

    @GetMapping("/purchase-request/{purchaseRequestId}")
    public ResponseEntity<List<SupplierQuote>> getQuotesByPurchaseRequest(@PathVariable String purchaseRequestId) {
        return ResponseEntity.ok(supplierQuoteService.getQuotesByPurchaseRequest(purchaseRequestId));
    }

    @GetMapping("/purchase-request/{purchaseRequestId}/sorted")
    public ResponseEntity<List<SupplierQuote>> getSortedQuotes(@PathVariable String purchaseRequestId) {
        return ResponseEntity.ok(supplierQuoteService.getSortedQuotes(purchaseRequestId));
    }

    @PostMapping
    public ResponseEntity<SupplierQuote> createQuote(@RequestBody SupplierQuote quote) {
        return ResponseEntity.ok(supplierQuoteService.createQuote(quote));
    }

    @PostMapping("/{id}/select")
    public ResponseEntity<SupplierQuote> selectQuote(@PathVariable String id) {
        SupplierQuote quote = supplierQuoteService.selectQuote(id);
        return quote != null ? ResponseEntity.ok(quote) : ResponseEntity.notFound().build();
    }
}
