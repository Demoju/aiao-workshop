package com.tableorder.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MenuResponseDto {
    private Long menuId;
    private String menuName;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private Boolean isAvailable;
}
