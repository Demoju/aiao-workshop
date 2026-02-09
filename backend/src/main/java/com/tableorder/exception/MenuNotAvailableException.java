package com.tableorder.exception;

public class MenuNotAvailableException extends BusinessException {
    public MenuNotAvailableException() {
        super(ErrorCode.MENU_NOT_AVAILABLE);
    }
    
    public MenuNotAvailableException(String message) {
        super(ErrorCode.MENU_NOT_AVAILABLE, message);
    }
}
