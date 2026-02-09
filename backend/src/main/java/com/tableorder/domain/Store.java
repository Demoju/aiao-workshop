package com.tableorder.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Store {
    private Long storeId;
    private String storeName;
    private String storeCode;
    private LocalDateTime createdAt;
}
