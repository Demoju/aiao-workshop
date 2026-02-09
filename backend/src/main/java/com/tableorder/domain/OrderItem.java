package com.tableorder.domain;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderItem {
    private Long orderItemId;
    private Long orderId;
    private Long menuId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private LocalDateTime createdAt;
    
    // Transient field (not stored in DB)
    private String menuName;
}
