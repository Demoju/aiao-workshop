package com.tableorder.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
    private Long categoryId;
    private String categoryName;
    private Long storeId;
    private LocalDateTime createdAt;
}
