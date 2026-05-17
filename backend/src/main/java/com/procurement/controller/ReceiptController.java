package com.procurement.controller;

import com.procurement.model.Receipt;
import com.procurement.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {
    @Autowired
    private ReceiptService receiptService;

    @GetMapping("/order/{purchaseOrderId}")
    public ResponseEntity<List<Receipt>> getReceiptsByOrder(@PathVariable String purchaseOrderId) {
        return ResponseEntity.ok(receiptService.getReceiptsByOrder(purchaseOrderId));
    }

    @PostMapping
    public ResponseEntity<Receipt> createReceipt(@RequestBody Receipt receipt) {
        return ResponseEntity.ok(receiptService.createReceipt(receipt));
    }
}
