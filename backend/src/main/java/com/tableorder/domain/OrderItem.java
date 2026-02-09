package com.tableorder.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
