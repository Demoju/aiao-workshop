package com.tableorder.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponseDto {
    private Integer status;
    private String code;
    private String message;
    private String timestamp;
    private String path;
}
