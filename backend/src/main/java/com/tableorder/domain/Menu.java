package com.tableorder.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Menu {
    private Long menuId;
    private String menuName;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private Long storeId;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
