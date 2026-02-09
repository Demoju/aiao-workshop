# Business Rules - Unit 2 (Backend Customer API)

## Overview

Backend Customer API에서 적용되는 비즈니스 규칙 및 검증 로직을 정의합니다.

---

## 1. 인증 검증 규칙

### BR-AUTH-001: 테이블 로그인 필수 입력
- **규칙**: storeId, tableNumber, password 모두 필수
- **검증 시점**: API 요청 수신 시 (@Valid 어노테이션)
- **에러 응답**: 400 Bad Request
- **에러 메시지**: "필수 입력 항목이 누락되었습니다"

### BR-AUTH-002: 테이블 존재 여부 확인
- **규칙**: storeId와 tableNumber로 Table 조회 가능해야 함
- **검증 시점**: 로그인 요청 처리 시
- **에러 응답**: 401 Unauthorized
- **에러 메시지**: "테이블을 찾을 수 없습니다"

### BR-AUTH-003: 비밀번호 검증
- **규칙**: bcrypt 해싱으로 비밀번호 비교
- **검증 시점**: 로그인 요청 처리 시
- **에러 응답**: 401 Unauthorized
- **에러 메시지**: "비밀번호가 일치하지 않습니다"

### BR-AUTH-004: 세션 자동 생성
- **규칙**: 활성 세션이 없으면 자동으로 새 세션 생성
- **검증 시점**: 로그인 성공 시
- **처리**: TableSession 생성 (startTime = 현재, isActive = true)

---

## 2. 메뉴 조회 규칙

### BR-MENU-001: 판매 가능 메뉴만 조회
- **규칙**: isAvailable = true인 메뉴만 반환
- **검증 시점**: 메뉴 목록 조회 시
- **처리**: WHERE isAvailable = true 조건 추가

### BR-MENU-002: 카테고리 필터링
- **규칙**: categoryId 제공 시 해당 카테고리 메뉴만 반환
- **검증 시점**: 메뉴 목록 조회 시
- **처리**: WHERE categoryId = ? 조건 추가

### BR-MENU-003: 메뉴 정보 포함
- **규칙**: 메뉴 조회 시 이미지 URL 포함
- **검증 시점**: 응답 생성 시
- **처리**: MenuResponseDto에 imageUrl 포함

---

## 3. 주문 생성 검증 규칙

### BR-ORDER-001: 필수 입력 검증
- **규칙**: tableId, sessionId, items, totalAmount 모두 필수
- **검증 시점**: API 요청 수신 시
- **에러 응답**: 400 Bad Request
- **에러 메시지**: "필수 입력 항목이 누락되었습니다"

### BR-ORDER-002: 빈 주문 항목 검증
- **규칙**: items 배열이 비어있으면 안 됨
- **검증 시점**: 주문 생성 요청 처리 시
- **에러 응답**: 400 Bad Request
- **에러 메시지**: "주문 항목이 비어있습니다"

### BR-ORDER-003: 메뉴 존재 여부 확인
- **규칙**: 각 OrderItem의 menuId가 DB에 존재해야 함
- **검증 시점**: 주문 생성 요청 처리 시
- **에러 응답**: 404 Not Found
- **에러 메시지**: "메뉴 ID {menuId}를 찾을 수 없습니다"

### BR-ORDER-004: 메뉴 판매 가능 여부 확인
- **규칙**: 주문하려는 메뉴의 isAvailable = true여야 함
- **검증 시점**: 주문 생성 요청 처리 시
- **에러 응답**: 400 Bad Request
- **에러 메시지**: "품절된 메뉴입니다: {menuName}"

### BR-ORDER-005: 가격 검증
- **규칙**: Frontend에서 전달한 unitPrice와 DB의 Menu.price가 일치해야 함
- **검증 시점**: 주문 생성 요청 처리 시
- **에러 응답**: 400 Bad Request
- **에러 메시지**: "메뉴 가격이 변경되었습니다. 다시 시도해주세요"
- **처리**: 가격 불일치 시 주문 생성 중단

### BR-ORDER-006: 총액 검증
- **규칙**: Σ(unitPrice × quantity) = totalAmount
- **검증 시점**: 주문 생성 요청 처리 시
- **에러 응답**: 400 Bad Request
- **에러 메시지**: "주문 금액이 일치하지 않습니다"
- **처리**: 총액 불일치 시 주문 생성 중단

### BR-ORDER-007: 수량 검증
- **규칙**: 각 OrderItem의 quantity > 0
- **검증 시점**: 주문 생성 요청 처리 시
- **에러 응답**: 400 Bad Request
- **에러 메시지**: "수량은 1개 이상이어야 합니다"

### BR-ORDER-008: 최소 주문 금액 제한
- **규칙**: 제한 없음
- **검증 시점**: N/A
- **처리**: 검증 안 함

### BR-ORDER-009: 최대 수량 제한
- **규칙**: 제한 없음 (Frontend에서만 제한)
- **검증 시점**: N/A
- **처리**: 검증 안 함

### BR-ORDER-010: 동일 메뉴 수량 합산
- **규칙**: 동일한 menuId가 여러 개 있으면 수량 합산하여 하나의 OrderItem으로 생성
- **검증 시점**: 주문 생성 요청 처리 시
- **처리**: 
  - items를 menuId로 그룹핑
  - 동일 menuId의 quantity 합산
  - 하나의 OrderItem 생성

---

## 4. 주문 번호 생성 규칙

### BR-ORDERNUM-001: 주문 번호 형식
- **규칙**: "ORD-YYYYMMDD-NNN" 형식
- **생성 시점**: 주문 생성 시
- **예시**: "ORD-20260209-001"

### BR-ORDERNUM-002: 순번 생성
- **규칙**: 당일 주문 개수 + 1
- **생성 시점**: 주문 생성 시
- **처리**: 
  - SELECT COUNT(*) FROM orders WHERE DATE(order_time) = CURRENT_DATE
  - 순번 = count + 1
  - 3자리 문자열로 변환 (001, 002, ...)

### BR-ORDERNUM-003: 주문 번호 고유성
- **규칙**: 주문 번호는 고유해야 함 (UNIQUE 제약 조건)
- **검증 시점**: Database 저장 시
- **처리**: 중복 발생 시 재시도 (최대 3회)

---

## 5. 주문 상태 규칙

### BR-STATUS-001: 초기 상태
- **규칙**: 주문 생성 시 status = PENDING
- **설정 시점**: 주문 생성 시
- **처리**: Order 엔티티 생성 시 status 필드 설정

### BR-STATUS-002: 상태 값
- **규칙**: 허용된 상태 값
  - PENDING: 대기중
  - PREPARING: 준비중
  - COMPLETED: 완료
  - CANCELLED: 취소됨
- **검증 시점**: 상태 변경 시 (Admin API)
- **처리**: Enum으로 정의

---

## 6. 주문 취소 규칙

### BR-CANCEL-001: 주문 존재 여부 확인
- **규칙**: orderId로 Order 조회 가능해야 함
- **검증 시점**: 주문 취소 요청 처리 시
- **에러 응답**: 404 Not Found
- **에러 메시지**: "주문을 찾을 수 없습니다"

### BR-CANCEL-002: 취소 가능 상태 확인
- **규칙**: status = PENDING인 주문만 취소 가능
- **검증 시점**: 주문 취소 요청 처리 시
- **에러 응답**: 400 Bad Request
- **에러 메시지**: "대기 중인 주문만 취소할 수 있습니다"
- **처리**: status ≠ PENDING이면 취소 중단

### BR-CANCEL-003: 주문 삭제
- **규칙**: 주문 취소 시 Order와 OrderItem 모두 삭제
- **검증 시점**: 주문 취소 요청 처리 시
- **처리**: 
  - OrderItem 먼저 삭제
  - Order 삭제
  - Transaction으로 원자성 보장

---

## 7. 주문 내역 조회 규칙

### BR-HISTORY-001: 세션 필터링
- **규칙**: 현재 활성 세션의 주문만 조회
- **검증 시점**: 주문 내역 조회 시
- **처리**: WHERE sessionId = ? AND isActive = true

### BR-HISTORY-002: 시간 정렬
- **규칙**: 주문 시각 역순 정렬 (최신 주문이 위)
- **검증 시점**: 주문 내역 조회 시
- **처리**: ORDER BY order_time DESC

### BR-HISTORY-003: OrderItem 정보 포함
- **규칙**: 각 Order에 OrderItem 리스트 포함
- **검증 시점**: 응답 생성 시
- **처리**: 
  - Order와 OrderItem JOIN
  - Menu 테이블 JOIN하여 menuName 포함

### BR-HISTORY-004: 빈 결과 처리
- **규칙**: 주문 내역이 없으면 빈 배열 반환
- **검증 시점**: 주문 내역 조회 시
- **처리**: 빈 리스트 반환 (에러 아님)

---

## 8. 테이블 세션 규칙

### BR-SESSION-001: 세션 자동 생성
- **규칙**: 주문 생성 시 활성 세션이 없으면 자동 생성
- **검증 시점**: 주문 생성 요청 처리 시
- **처리**: 
  - sessionId로 TableSession 조회
  - 없거나 isActive = false이면 새 세션 생성

### BR-SESSION-002: 세션 ID 생성
- **규칙**: UUID 형식으로 생성
- **생성 시점**: 세션 생성 시
- **처리**: UUID.randomUUID().toString()

### BR-SESSION-003: 세션 활성 상태
- **규칙**: 새 세션 생성 시 isActive = true
- **설정 시점**: 세션 생성 시
- **처리**: TableSession 엔티티 생성 시 isActive 필드 설정

### BR-SESSION-004: 세션 시작 시각
- **규칙**: 세션 생성 시 startTime = 현재 시각
- **설정 시점**: 세션 생성 시
- **처리**: TableSession 엔티티 생성 시 startTime 필드 설정

---

## 9. 데이터 무결성 규칙

### BR-INTEGRITY-001: Transaction 경계
- **규칙**: 주문 생성 및 취소는 Transaction으로 묶음
- **적용 시점**: 주문 생성, 주문 취소
- **처리**: @Transactional 어노테이션 사용

### BR-INTEGRITY-002: Cascade Delete
- **규칙**: Order 삭제 시 OrderItem도 함께 삭제
- **적용 시점**: 주문 취소 시
- **처리**: 
  - MyBatis: 명시적으로 OrderItem 먼저 삭제
  - 또는 Database FK Cascade 설정

### BR-INTEGRITY-003: Foreign Key 제약
- **규칙**: 
  - Order.tableId → Table.tableId
  - Order.sessionId → TableSession.sessionId
  - OrderItem.orderId → Order.orderId
  - OrderItem.menuId → Menu.menuId
- **적용 시점**: Database 스키마 정의 시
- **처리**: Foreign Key 제약 조건 설정

---

## 10. 에러 처리 규칙

### BR-ERROR-001: 표준 에러 응답
- **규칙**: 모든 에러는 ErrorResponseDto 형식으로 반환
- **적용 시점**: 예외 발생 시
- **처리**: @ControllerAdvice에서 전역 예외 처리

### BR-ERROR-002: HTTP 상태 코드 매핑
- **규칙**: 
  - 400: 잘못된 요청 (검증 실패, 가격 불일치 등)
  - 401: 인증 실패 (로그인 실패)
  - 404: 리소스 없음 (메뉴 없음, 주문 없음)
  - 500: 서버 에러
- **적용 시점**: 예외 발생 시
- **처리**: 예외 타입별로 HTTP 상태 코드 매핑

### BR-ERROR-003: 사용자 친화적 에러 메시지
- **규칙**: 에러 메시지는 사용자가 이해할 수 있는 한국어로 작성
- **적용 시점**: 예외 발생 시
- **예시**: 
  - "테이블을 찾을 수 없습니다"
  - "메뉴 가격이 변경되었습니다"
  - "대기 중인 주문만 취소할 수 있습니다"

### BR-ERROR-004: 에러 로깅
- **규칙**: 모든 에러는 로그에 기록
- **적용 시점**: 예외 발생 시
- **처리**: 
  - ERROR 레벨: 500 에러
  - WARN 레벨: 400, 401, 404 에러
  - 로그 포맷: [timestamp] [level] [class] [message] [stacktrace]

---

## 11. 중복 요청 방지 규칙

### BR-IDEMPOTENCY-001: Idempotency Key 지원
- **규칙**: HTTP Header "Idempotency-Key" 지원
- **적용 시점**: 주문 생성 요청 처리 시
- **처리**: 
  - Idempotency-Key 있으면 Redis에서 조회
  - 캐시 있으면 캐시된 응답 반환
  - 캐시 없으면 주문 생성 후 Redis에 저장 (TTL: 24시간)

### BR-IDEMPOTENCY-002: Idempotency Key 선택적
- **규칙**: Idempotency-Key는 선택적 (없어도 정상 처리)
- **적용 시점**: 주문 생성 요청 처리 시
- **처리**: Header 없으면 중복 방지 안 함

---

## 12. Service 레이어 통합 규칙

### BR-SERVICE-001: OrderService 공유
- **규칙**: CustomerService는 OrderService의 주문 생성 로직 사용
- **적용 시점**: 주문 생성 요청 처리 시
- **처리**: CustomerService에서 OrderService.createOrder() 호출

### BR-SERVICE-002: MenuService 공유
- **규칙**: CustomerService와 AdminService는 동일한 MenuService 사용
- **적용 시점**: 메뉴 조회 시
- **처리**: 두 Service 모두 MenuService 의존성 주입

### BR-SERVICE-003: TableSessionService 공유
- **규칙**: CustomerService와 AdminService는 동일한 TableSessionService 사용
- **적용 시점**: 세션 관리 시
- **처리**: 두 Service 모두 TableSessionService 의존성 주입

---

## Validation Summary

| 규칙 ID | 검증 항목 | 검증 시점 | 에러 처리 |
|---------|----------|----------|----------|
| BR-AUTH-002 | 테이블 존재 | 로그인 시 | 401 Unauthorized |
| BR-AUTH-003 | 비밀번호 검증 | 로그인 시 | 401 Unauthorized |
| BR-ORDER-003 | 메뉴 존재 | 주문 생성 시 | 404 Not Found |
| BR-ORDER-004 | 메뉴 판매 가능 | 주문 생성 시 | 400 Bad Request |
| BR-ORDER-005 | 가격 검증 | 주문 생성 시 | 400 Bad Request |
| BR-ORDER-006 | 총액 검증 | 주문 생성 시 | 400 Bad Request |
| BR-CANCEL-002 | 취소 가능 상태 | 주문 취소 시 | 400 Bad Request |

---

## Notes

- 모든 검증 규칙은 데이터 무결성과 보안을 고려하여 설계
- Backend 검증은 보안 및 데이터 무결성 보장이 목적
- Frontend 검증은 UX 개선용, Backend 검증은 필수

