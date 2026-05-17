package com.procurement.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Supplier {
    private String id;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private BigDecimal rating;
    private Integer totalOrders;
    private Integer delayCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
