package com.tableorder.customer.dto;

import com.tableorder.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private String orderNumber;
    private Long tableId;
    private String sessionId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime orderTime;
    private List<OrderItemResponseDto> items;
}
