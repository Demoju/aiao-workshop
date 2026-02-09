package com.tableorder.exception;

public class TotalAmountMismatchException extends BusinessException {
    public TotalAmountMismatchException() {
        super(ErrorCode.TOTAL_AMOUNT_MISMATCH);
    }
}
