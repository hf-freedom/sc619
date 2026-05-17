package com.procurement.service;

import com.procurement.model.SupplierQuote;
import com.procurement.model.PurchaseRequest;
import com.procurement.model.Supplier;
import com.procurement.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SupplierQuoteService {
    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private SupplierService supplierService;

    public List<SupplierQuote> getQuotesByPurchaseRequest(String purchaseRequestId) {
        return storage.supplierQuotes.values().stream()
                .filter(q -> q.getPurchaseRequestId().equals(purchaseRequestId))
                .collect(Collectors.toList());
    }

    public List<SupplierQuote> getSortedQuotes(String purchaseRequestId) {
        return getQuotesByPurchaseRequest(purchaseRequestId).stream()
                .sorted(Comparator.comparing(SupplierQuote::getQuotedPrice)
                        .thenComparing(SupplierQuote::getDeliveryDays))
                .collect(Collectors.toList());
    }

    public SupplierQuote createQuote(SupplierQuote quote) {
        quote.setId(UUID.randomUUID().toString());
        quote.setStatus(SupplierQuote.Status.SUBMITTED);
        quote.setCreatedAt(LocalDateTime.now());
        quote.setUpdatedAt(LocalDateTime.now());
        
        Supplier supplier = supplierService.getSupplierById(quote.getSupplierId());
        if (supplier != null) {
            quote.setSupplierName(supplier.getName());
        }
        
        storage.supplierQuotes.put(quote.getId(), quote);
        return quote;
    }

    public SupplierQuote selectQuote(String quoteId) {
        SupplierQuote quote = storage.supplierQuotes.get(quoteId);
        if (quote == null) return null;

        quote.setStatus(SupplierQuote.Status.SELECTED);
        quote.setUpdatedAt(LocalDateTime.now());

        getQuotesByPurchaseRequest(quote.getPurchaseRequestId()).stream()
                .filter(q -> !q.getId().equals(quoteId))
                .forEach(q -> {
                    q.setStatus(SupplierQuote.Status.REJECTED);
                    q.setUpdatedAt(LocalDateTime.now());
                });

        PurchaseRequest request = storage.purchaseRequests.get(quote.getPurchaseRequestId());
        if (request != null) {
            request.setStatus(PurchaseRequest.Status.QUOTATION_COMPLETED);
            request.setSelectedSupplierId(quote.getSupplierId());
            request.setUpdatedAt(LocalDateTime.now());
        }

        return quote;
    }

    public void cancelQuotesByPurchaseRequest(String purchaseRequestId) {
        getQuotesByPurchaseRequest(purchaseRequestId).forEach(q -> {
            q.setStatus(SupplierQuote.Status.CANCELLED);
            q.setUpdatedAt(LocalDateTime.now());
        });
    }
}
