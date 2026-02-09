# Business Logic Model - Unit 2 (Backend Customer API)

## Overview

Backend Customer API의 핵심 비즈니스 로직을 정의합니다. 이 문서는 기술 구현과 무관하게 비즈니스 흐름과 규칙을 설명합니다.

---

## 1. 테이블 로그인 로직

### 1.1 로그인 검증 플로우

```
Input: TableLoginRequestDto { storeId, tableNumber, password }
Process:
  1. storeId와 tableNumber로 Table 조회
  2. IF Table 존재하지 않음:
       401 Unauthorized 반환
       에러 메시지: "테이블을 찾을 수 없습니다"
  3. bcrypt로 password 검증
  4. IF 비밀번호 불일치:
       401 Unauthorized 반환
       에러 메시지: "비밀번호가 일치하지 않습니다"
  5. 활성 TableSession 조회
  6. IF 활성 세션 없음:
       새 TableSession 생성 (startTime = 현재 시각, isActive = true)
  7. TableLoginResponseDto 생성 및 반환
Output: TableLoginResponseDto { tableId, sessionId, storeId, tableNumber }
```

**설계 근거**: bcrypt 해싱으로 비밀번호 보안 강화, 구체적 에러 메시지로 UX 개선

---

## 2. 메뉴 조회 로직

### 2.1 메뉴 목록 조회

```
Input: categoryId (optional)
Process:
  1. IF categoryId 제공:
       해당 카테고리의 메뉴만 조회
     ELSE:
       모든 메뉴 조회
  2. isAvailable = true인 메뉴만 필터링
  3. MenuResponseDto 리스트 생성
Output: List<MenuResponseDto>
```

### 2.2 MenuResponseDto 구조

```java
{
  menuId: Long,
  menuName: String,
  price: BigDecimal,
  description: String,
  imageUrl: String,
  categoryId: Long,
  isAvailable: Boolean
}
```

**설계 근거**: 이미지 URL 포함하여 Frontend에서 추가 요청 불필요, 품절 메뉴 필터링

---

## 3. 주문 생성 로직

### 3.1 주문 생성 플로우

```
Input: OrderRequestDto {
  tableId,
  sessionId,
  items: [{ menuId, quantity, unitPrice }],
  totalAmount
}

Process:
  1. 테이블 세션 검증:
     - sessionId로 TableSession 조회
     - IF 세션 없음:
         자동으로 새 세션 생성
     - IF 세션 종료됨 (isActive = false):
         새 세션 생성
  
  2. 메뉴 검증:
     - 각 OrderItem의 menuId로 Menu 조회
     - IF 메뉴 존재하지 않음:
         404 Not Found 반환
         에러 메시지: "메뉴 ID {menuId}를 찾을 수 없습니다"
     - IF 메뉴 품절 (isAvailable = false):
         400 Bad Request 반환
         에러 메시지: "품절된 메뉴입니다: {menuName}"
  
  3. 가격 검증:
     - Frontend에서 전달한 unitPrice와 DB의 Menu.price 비교
     - IF 가격 불일치:
         400 Bad Request 반환
         에러 메시지: "메뉴 가격이 변경되었습니다. 다시 시도해주세요"
  
  4. 총액 검증:
     - 계산: Σ(unitPrice × quantity)
     - IF 계산 결과 ≠ totalAmount:
         400 Bad Request 반환
         에러 메시지: "주문 금액이 일치하지 않습니다"
  
  5. 주문 번호 생성:
     - 형식: ORD-YYYYMMDD-NNN
     - 예시: ORD-20260209-001
     - 당일 주문 순번으로 NNN 생성 (001부터 시작)
  
  6. 동일 메뉴 수량 합산:
     - OrderRequestDto.items에서 동일 menuId 검색
     - 동일 menuId 있으면 quantity 합산
     - 하나의 OrderItem으로 생성
  
  7. Order 엔티티 생성:
     - orderId: Auto-increment
     - orderNumber: 생성된 주문 번호
     - tableId: 요청의 tableId
     - sessionId: 요청의 sessionId
     - totalAmount: 검증된 totalAmount
     - status: PENDING
     - orderTime: 현재 시각
  
  8. OrderItem 엔티티 생성:
     - 각 item에 대해 OrderItem 생성
     - unitPrice 저장 (주문 시점 가격 기록)
  
  9. Database 저장:
     - Order 저장
     - OrderItem 리스트 저장
     - Transaction으로 묶어서 원자성 보장
  
  10. OrderResponseDto 생성 및 반환

Output: OrderResponseDto {
  orderId,
  orderNumber,
  tableId,
  sessionId,
  totalAmount,
  status,
  orderTime,
  items: [OrderItem]
}
```

**설계 근거**:
- 세션 자동 생성으로 UX 개선
- 가격 검증으로 보안 강화
- 동일 메뉴 수량 합산으로 데이터 정규화
- 주문 번호 날짜 형식으로 사용자 친화적

---

## 4. 주문 내역 조회 로직

### 4.1 주문 목록 조회

```
Input: tableId, sessionId
Process:
  1. sessionId로 현재 활성 세션 확인
  2. IF 세션 없음 OR 세션 종료됨:
       빈 리스트 반환
  3. tableId와 sessionId로 Order 조회
  4. 각 Order에 대해 OrderItem 조회 (JOIN)
  5. 시간 역순 정렬 (orderTime DESC)
  6. OrderResponseDto 리스트 생성
Output: List<OrderResponseDto>
```

### 4.2 OrderItem 정보 포함

```java
OrderItem {
  orderItemId: Long,
  menuId: Long,
  menuName: String,  // Menu 테이블 JOIN
  quantity: Integer,
  unitPrice: BigDecimal
}
```

**설계 근거**: menuName 포함하여 Frontend에서 추가 조회 불필요

---

## 5. 주문 취소 로직

### 5.1 주문 취소 플로우

```
Input: orderId
Process:
  1. orderId로 Order 조회
  2. IF Order 존재하지 않음:
       404 Not Found 반환
       에러 메시지: "주문을 찾을 수 없습니다"
  
  3. 주문 상태 확인:
     - IF status ≠ PENDING:
         400 Bad Request 반환
         에러 메시지: "대기 중인 주문만 취소할 수 있습니다"
  
  4. Order 삭제:
     - OrderItem 먼저 삭제 (Cascade Delete 또는 명시적 삭제)
     - Order 삭제
     - Transaction으로 묶어서 원자성 보장
  
  5. 성공 응답 반환 (204 No Content)

Output: 204 No Content
```

**설계 근거**: PENDING 상태만 취소 가능하여 주방 혼란 방지

---

## 6. 카테고리 조회 로직

### 6.1 카테고리 목록 조회

```
Input: None
Process:
  1. 모든 Category 조회
  2. CategoryResponseDto 리스트 생성
Output: List<CategoryResponseDto>
```

### 6.2 CategoryResponseDto 구조

```java
{
  categoryId: Long,
  categoryName: String,
  storeId: Long
}
```

---

## 7. 테이블 세션 관리 로직

### 7.1 세션 자동 생성

```
Trigger: 주문 생성 시 활성 세션 없음
Process:
  1. TableSession 엔티티 생성:
     - sessionId: UUID 생성
     - tableId: 요청의 tableId
     - startTime: 현재 시각
     - endTime: null
     - isActive: true
  2. Database 저장
Output: TableSession
```

### 7.2 세션 검증

```
Input: sessionId
Process:
  1. sessionId로 TableSession 조회
  2. IF 세션 없음:
       return false
  3. IF isActive = false:
       return false
  4. return true
Output: Boolean
```

---

## 8. 가격 검증 로직

### 8.1 단가 검증

```
Input: menuId, clientUnitPrice (Frontend에서 전달)
Process:
  1. menuId로 Menu 조회
  2. IF Menu.price ≠ clientUnitPrice:
       throw PriceChangedException
  3. return true
Output: Boolean or Exception
```

**설계 근거**: 가격 변조 방지, 가격 변경 시 사용자에게 알림

### 8.2 총액 검증

```
Input: items, clientTotalAmount
Process:
  1. 계산: serverTotalAmount = Σ(item.unitPrice × item.quantity)
  2. IF serverTotalAmount ≠ clientTotalAmount:
       throw TotalAmountMismatchException
  3. return true
Output: Boolean or Exception
```

---

## 9. 주문 번호 생성 로직

### 9.1 주문 번호 생성 알고리즘

```
Process:
  1. 현재 날짜 가져오기: YYYYMMDD
  2. 당일 주문 개수 조회:
     - SELECT COUNT(*) FROM orders 
       WHERE DATE(order_time) = CURRENT_DATE
  3. 순번 계산: count + 1
  4. 순번을 3자리 문자열로 변환 (001, 002, ...)
  5. 주문 번호 조합: "ORD-" + YYYYMMDD + "-" + 순번
Output: String (예: "ORD-20260209-001")
```

**동시성 고려**:
- Database의 UNIQUE 제약 조건으로 중복 방지
- 중복 발생 시 재시도 (최대 3회)

---

## 10. 중복 요청 처리 로직 (Idempotency)

### 10.1 Idempotency Key 검증

```
Input: idempotencyKey (HTTP Header)
Process:
  1. IF idempotencyKey 없음:
       정상 처리 (중복 방지 안 함)
  
  2. Redis에서 idempotencyKey 조회:
     - Key: "idempotency:{idempotencyKey}"
     - Value: OrderResponseDto (JSON)
  
  3. IF Redis에 존재:
       캐시된 OrderResponseDto 반환 (중복 요청)
  
  4. ELSE:
       주문 생성 로직 실행
       생성된 OrderResponseDto를 Redis에 저장 (TTL: 24시간)
       OrderResponseDto 반환

Output: OrderResponseDto
```

**설계 근거**: 네트워크 오류로 인한 중복 주문 방지

---

## 11. Service 레이어 통합

### 11.1 CustomerService와 OrderService 통합

```java
@Service
public class CustomerService {
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private TableSessionService tableSessionService;
    
    public OrderResponseDto createOrder(OrderRequestDto request) {
        // 1. 세션 검증 및 생성
        tableSessionService.ensureActiveSession(request.getTableId(), request.getSessionId());
        
        // 2. 메뉴 검증
        menuService.validateMenus(request.getItems());
        
        // 3. 주문 생성 (OrderService에 위임)
        return orderService.createOrder(request);
    }
}
```

**설계 근거**: 주문 생성 로직을 OrderService에 위임하여 코드 중복 제거, AdminService와 공유

---

## 12. 에러 처리 로직

### 12.1 표준 에러 응답

```java
ErrorResponseDto {
  status: Integer,        // HTTP 상태 코드
  message: String,        // 사용자 친화적 에러 메시지
  timestamp: String,      // ISO 8601 형식
  path: String           // 요청 경로
}
```

### 12.2 에러 타입별 처리

| 에러 타입 | HTTP 상태 | 메시지 예시 |
|----------|----------|------------|
| 테이블 없음 | 401 | "테이블을 찾을 수 없습니다" |
| 비밀번호 틀림 | 401 | "비밀번호가 일치하지 않습니다" |
| 메뉴 없음 | 404 | "메뉴 ID {id}를 찾을 수 없습니다" |
| 메뉴 품절 | 400 | "품절된 메뉴입니다: {name}" |
| 가격 변경 | 400 | "메뉴 가격이 변경되었습니다" |
| 총액 불일치 | 400 | "주문 금액이 일치하지 않습니다" |
| 취소 불가 | 400 | "대기 중인 주문만 취소할 수 있습니다" |

---

## Notes

- 모든 비즈니스 로직은 기술 구현과 독립적으로 설계
- Transaction 경계는 주문 생성 및 취소 시 명확히 정의
- 상세 구현은 Code Generation 단계에서 진행

