package com.tableorder.exception;

public class InvalidOrderStatusException extends BusinessException {
    public InvalidOrderStatusException() {
        super(ErrorCode.INVALID_ORDER_STATUS);
    }
    
    public InvalidOrderStatusException(String message) {
        super(ErrorCode.INVALID_ORDER_STATUS, message);
    }
}
