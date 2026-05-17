package com.procurement.service;

import com.procurement.model.Supplier;
import com.procurement.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {
    @Autowired
    private InMemoryStorage storage;

    public List<Supplier> getAllSuppliers() {
        return new ArrayList<>(storage.suppliers.values());
    }

    public Supplier getSupplierById(String id) {
        return storage.suppliers.get(id);
    }

    public Supplier createSupplier(Supplier supplier) {
        supplier.setId(UUID.randomUUID().toString());
        supplier.setCreatedAt(LocalDateTime.now());
        supplier.setUpdatedAt(LocalDateTime.now());
        if (supplier.getRating() == null) supplier.setRating(BigDecimal.ZERO);
        if (supplier.getTotalOrders() == null) supplier.setTotalOrders(0);
        if (supplier.getDelayCount() == null) supplier.setDelayCount(0);
        storage.suppliers.put(supplier.getId(), supplier);
        return supplier;
    }

    public void incrementDelayCount(String supplierId) {
        Supplier supplier = storage.suppliers.get(supplierId);
        if (supplier != null) {
            supplier.setDelayCount(supplier.getDelayCount() + 1);
            supplier.setUpdatedAt(LocalDateTime.now());
        }
    }

    public void incrementOrderCount(String supplierId) {
        Supplier supplier = storage.suppliers.get(supplierId);
        if (supplier != null) {
            supplier.setTotalOrders(supplier.getTotalOrders() + 1);
            supplier.setUpdatedAt(LocalDateTime.now());
        }
    }
}
