package com.tableorder.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableSession {
    private Long id;
    private Long storeId;
    private Long tableId;
    private String sessionId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Boolean isActive;
}
