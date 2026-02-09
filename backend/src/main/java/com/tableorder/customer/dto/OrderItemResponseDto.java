package com.tableorder.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class OrderItemResponseDto {
    private Long orderItemId;
    private Long menuId;
    private String menuName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
