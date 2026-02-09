package com.tableorder.customer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDto {
    @NotNull(message = "스토어 ID는 필수입니다")
    private Long storeId;
    
    @NotNull(message = "테이블 ID는 필수입니다")
    private Long tableId;
    
    @NotBlank(message = "세션 ID는 필수입니다")
    private String sessionId;
    
    @NotEmpty(message = "주문 항목은 필수입니다")
    @Valid
    private List<OrderItemRequestDto> items;
    
    @NotNull(message = "총 금액은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "총 금액은 0보다 커야 합니다")
    private BigDecimal totalAmount;
    
    private String idempotencyKey;
}
