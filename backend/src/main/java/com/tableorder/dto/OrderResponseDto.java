package com.tableorder.dto;

import com.tableorder.domain.OrderStatus;
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
public class OrderResponseDto {
    private Long id;
    private Long tableId;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
