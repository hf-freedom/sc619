package com.procurement.storage;

import com.procurement.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStorage {
    public final Map<String, Budget> budgets = new ConcurrentHashMap<>();
    public final Map<String, PurchaseRequest> purchaseRequests = new ConcurrentHashMap<>();
    public final Map<String, Supplier> suppliers = new ConcurrentHashMap<>();
    public final Map<String, SupplierQuote> supplierQuotes = new ConcurrentHashMap<>();
    public final Map<String, PurchaseOrder> purchaseOrders = new ConcurrentHashMap<>();
    public final Map<String, Receipt> receipts = new ConcurrentHashMap<>();
    public final Map<String, DelayRisk> delayRisks = new ConcurrentHashMap<>();

    public InMemoryStorage() {
        initSampleData();
    }

    private void initSampleData() {
        Budget budget1 = new Budget();
        budget1.setId(UUID.randomUUID().toString());
        budget1.setName("IT设备采购预算");
        budget1.setCode("BUDGET-IT-001");
        budget1.setTotalAmount(new BigDecimal("500000"));
        budget1.setUsedAmount(BigDecimal.ZERO);
        budget1.setFrozenAmount(BigDecimal.ZERO);
        budget1.setCreatedAt(LocalDateTime.now());
        budget1.setUpdatedAt(LocalDateTime.now());
        budgets.put(budget1.getId(), budget1);

        Budget budget2 = new Budget();
        budget2.setId(UUID.randomUUID().toString());
        budget2.setName("办公用品采购预算");
        budget2.setCode("BUDGET-OFFICE-001");
        budget2.setTotalAmount(new BigDecimal("100000"));
        budget2.setUsedAmount(BigDecimal.ZERO);
        budget2.setFrozenAmount(BigDecimal.ZERO);
        budget2.setCreatedAt(LocalDateTime.now());
        budget2.setUpdatedAt(LocalDateTime.now());
        budgets.put(budget2.getId(), budget2);

        Supplier supplier1 = new Supplier();
        supplier1.setId(UUID.randomUUID().toString());
        supplier1.setName("科技设备有限公司");
        supplier1.setContactPerson("张经理");
        supplier1.setPhone("13800138001");
        supplier1.setEmail("zhang@tech.com");
        supplier1.setAddress("北京市海淀区");
        supplier1.setRating(new BigDecimal("4.8"));
        supplier1.setTotalOrders(0);
        supplier1.setDelayCount(0);
        supplier1.setCreatedAt(LocalDateTime.now());
        supplier1.setUpdatedAt(LocalDateTime.now());
        suppliers.put(supplier1.getId(), supplier1);

        Supplier supplier2 = new Supplier();
        supplier2.setId(UUID.randomUUID().toString());
        supplier2.setName("创新电子科技");
        supplier2.setContactPerson("李总");
        supplier2.setPhone("13800138002");
        supplier2.setEmail("li@innovation.com");
        supplier2.setAddress("上海市浦东新区");
        supplier2.setRating(new BigDecimal("4.5"));
        supplier2.setTotalOrders(0);
        supplier2.setDelayCount(0);
        supplier2.setCreatedAt(LocalDateTime.now());
        supplier2.setUpdatedAt(LocalDateTime.now());
        suppliers.put(supplier2.getId(), supplier2);

        Supplier supplier3 = new Supplier();
        supplier3.setId(UUID.randomUUID().toString());
        supplier3.setName("优质办公用品");
        supplier3.setContactPerson("王女士");
        supplier3.setPhone("13800138003");
        supplier3.setEmail("wang@office.com");
        supplier3.setAddress("广州市天河区");
        supplier3.setRating(new BigDecimal("4.2"));
        supplier3.setTotalOrders(0);
        supplier3.setDelayCount(0);
        supplier3.setCreatedAt(LocalDateTime.now());
        supplier3.setUpdatedAt(LocalDateTime.now());
        suppliers.put(supplier3.getId(), supplier3);
    }
}
