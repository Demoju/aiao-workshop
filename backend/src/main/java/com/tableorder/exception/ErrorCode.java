package com.tableorder.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 인증 에러 (401)
    UNAUTHORIZED("AUTH001", "인증에 실패했습니다"),
    TABLE_NOT_FOUND("AUTH002", "테이블을 찾을 수 없습니다"),
    INVALID_PASSWORD("AUTH003", "비밀번호가 일치하지 않습니다"),
    
    // 비즈니스 에러 (400)
    MENU_NOT_FOUND("MENU001", "메뉴를 찾을 수 없습니다"),
    MENU_NOT_AVAILABLE("MENU002", "품절된 메뉴입니다"),
    PRICE_CHANGED("ORDER001", "메뉴 가격이 변경되었습니다"),
    TOTAL_AMOUNT_MISMATCH("ORDER002", "주문 금액이 일치하지 않습니다"),
    INVALID_ORDER_STATUS("ORDER003", "대기 중인 주문만 취소할 수 있습니다"),
    ORDER_NOT_FOUND("ORDER004", "주문을 찾을 수 없습니다"),
    
    // 서버 에러 (500)
    INTERNAL_SERVER_ERROR("SYS001", "서버 오류가 발생했습니다");
    
    private final String code;
    private final String message;
}
