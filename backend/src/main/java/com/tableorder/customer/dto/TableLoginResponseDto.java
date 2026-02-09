package com.tableorder.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableLoginResponseDto {
    private Long tableId;
    private String sessionId;
    private Long storeId;
    private String tableNumber;
}
