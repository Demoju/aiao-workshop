package com.tableorder.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Table {
    private Long tableId;
    private String tableNumber;
    private Long storeId;
    private String passwordHash;
    private LocalDateTime createdAt;
}
