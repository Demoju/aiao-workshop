package com.tableorder.exception;

public class MenuNotFoundException extends BusinessException {
    public MenuNotFoundException() {
        super(ErrorCode.MENU_NOT_FOUND);
    }
    
    public MenuNotFoundException(String message) {
        super(ErrorCode.MENU_NOT_FOUND, message);
    }
}
