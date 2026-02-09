package com.tableorder.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryResponseDto {
    private Long categoryId;
    private String categoryName;
    private Long storeId;
}
