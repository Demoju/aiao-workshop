package com.tableorder.exception;

public class PriceChangedException extends BusinessException {
    public PriceChangedException() {
        super(ErrorCode.PRICE_CHANGED);
    }
}
