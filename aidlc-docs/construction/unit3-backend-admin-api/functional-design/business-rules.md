# Business Rules - Unit 3 (Backend Admin API)

## Overview
Unit 3의 비즈니스 규칙 및 제약사항

---

## 1. Authentication Rules

### BR-AUTH-001: Password Verification
- **Rule**: 비밀번호 검증 실패 시 로그인 시도 횟수 증가
- **Implementation**: `loginAttempts++`
- **Exception**: 없음

### BR-AUTH-002: Account Locking
- **Rule**: 5회 연속 로그인 실패 시 계정 30분 잠금
- **Condition**: `loginAttempts >= 5`
- **Action**: 
  - `lockedUntil = now + 30 minutes`
  - Return error: "Account locked. Try again after 30 minutes"
- **Exception**: 없음

### BR-AUTH-003: Account Unlock
- **Rule**: lockedUntil 시각 이후 자동 잠금 해제
- **Condition**: `lockedUntil <= now`
- **Action**: 로그인 허용
- **Exception**: 없음

### BR-AUTH-004: Login Success
- **Rule**: 로그인 성공 시 시도 횟수 초기화
- **Action**: `loginAttempts = 0`
- **Exception**: 없음

### BR-AUTH-005: JWT Token Expiration
- **Rule**: JWT 토큰 16시간 만료, 갱신 없음
- **Implementation**: `expiresIn = 16 hours`
- **Exception**: 없음

---

## 2. Order Status Transition Rules

### BR-ORDER-001: Sequential Status Transition
- **Rule**: 주문 상태는 순차적으로만 전환 가능
- **Allowed Transitions**:
  - `PENDING → PREPARING`
  - `PREPARING → COMPLETED`
- **Forbidden Transitions**:
  - `PREPARING → PENDING`
  - `COMPLETED → PREPARING`
  - `COMPLETED → PENDING`
- **Exception**: 취소는 언제든 가능 (BR-ORDER-002)

### BR-ORDER-002: Cancellation Allowed Anytime
- **Rule**: 취소는 모든 상태에서 가능
- **Allowed Transitions**:
  - `PENDING → CANCELLED`
  - `PREPARING → CANCELLED`
  - `COMPLETED → CANCELLED`
- **Exception**: 없음

### BR-ORDER-003: Invalid Transition Error
- **Rule**: 허용되지 않은 상태 전환 시 에러 반환
- **HTTP Status**: 400 Bad Request
- **Error Message**: "Invalid status transition from {current} to {target}"
- **Exception**: 없음

---

## 3. Order Deletion Rules

### BR-DELETE-001: Deletion Allowed Status
- **Rule**: 완료 또는 취소 상태만 삭제 가능
- **Condition**: `status IN (COMPLETED, CANCELLED)`
- **Action**: Hard delete (물리 삭제)
- **Exception**: 없음

### BR-DELETE-002: Deletion Forbidden Status
- **Rule**: 대기중 또는 준비중 상태는 삭제 불가
- **Condition**: `status IN (PENDING, PREPARING)`
- **HTTP Status**: 400 Bad Request
- **Error Message**: "Cannot delete order in {status} status"
- **Exception**: 없음

### BR-DELETE-003: Cascade Deletion
- **Rule**: 주문 삭제 시 주문 항목도 함께 삭제
- **Implementation**: `DELETE FROM order_item WHERE order_id = ?`
- **Exception**: 없음

---

## 4. Table Session Rules

### BR-SESSION-001: Active Session Uniqueness
- **Rule**: 테이블당 하나의 활성 세션만 존재
- **Condition**: `isActive = true`
- **Constraint**: UNIQUE constraint on (tableId, isActive) WHERE isActive = true
- **Exception**: 없음

### BR-SESSION-002: Session End Validation
- **Rule**: 세션 종료 전 미완료 주문 확인 필수
- **Condition**: `EXISTS (SELECT 1 FROM orders WHERE sessionId = ? AND status IN (PENDING, PREPARING))`
- **Action**: 
  - 미완료 주문 존재 시 에러 반환
  - HTTP Status: 400 Bad Request
  - Error Message: "Cannot end session with pending orders"
- **Exception**: 없음

### BR-SESSION-003: Session End Process
- **Rule**: 세션 종료 시 endedAt 설정 및 isActive = false
- **Action**:
  - `endedAt = now`
  - `isActive = false`
- **Exception**: 없음

### BR-SESSION-004: Session Start
- **Rule**: 첫 주문 생성 시 자동으로 세션 시작
- **Condition**: 활성 세션이 없는 경우
- **Action**: 
  - `startedAt = now`
  - `isActive = true`
- **Exception**: Customer API에서 처리 (Unit 2)

---

## 5. Past Orders Query Rules

### BR-PAST-001: Query Scope
- **Rule**: 모든 과거 주문 조회 가능 (페이지네이션)
- **Condition**: `isActive = false`
- **Implementation**: 
  ```sql
  SELECT * FROM orders 
  WHERE sessionId IN (
    SELECT id FROM table_session 
    WHERE tableId = ? AND isActive = false
  )
  ORDER BY createdAt DESC
  LIMIT ? OFFSET ?
  ```
- **Exception**: 없음

### BR-PAST-002: Pagination
- **Rule**: 페이지네이션 필수
- **Default**: page=0, size=20
- **Max Size**: 100
- **Exception**: 없음

---

## 6. SSE Connection Rules

### BR-SSE-001: Connection Timeout
- **Rule**: SSE 연결 무제한 유지
- **Implementation**: `timeout = Long.MAX_VALUE`
- **Exception**: 없음

### BR-SSE-002: Event Publishing Trigger
- **Rule**: 새 주문 생성 시에만 SSE 이벤트 발행
- **Trigger**: Customer API에서 주문 생성 완료
- **Event Type**: "new-order"
- **Exception**: 없음

### BR-SSE-003: Broadcast Strategy
- **Rule**: 모든 연결된 관리자에게 이벤트 브로드캐스트
- **Implementation**: 모든 활성 SseEmitter에 전송
- **Exception**: 전송 실패 시 해당 Emitter 제거

### BR-SSE-004: Connection Cleanup
- **Rule**: 에러 또는 타임아웃 시 Emitter 자동 제거
- **Trigger**: onError, onTimeout, onCompletion
- **Action**: `emitters.remove(adminId)`
- **Exception**: 없음

---

## 7. Authorization Rules

### BR-AUTH-006: Single Admin Model
- **Rule**: 단일 관리자 모델 (권한 구분 없음)
- **Implementation**: JWT 토큰 검증만 수행
- **Exception**: 없음

### BR-AUTH-007: JWT Validation
- **Rule**: 모든 Admin API 요청 시 JWT 토큰 검증 필수
- **Implementation**: JwtAuthenticationFilter
- **Exception**: `/api/admin/login` 엔드포인트는 제외

---

## 8. Concurrency Rules

### BR-CONC-001: Last-Write-Wins
- **Rule**: 동시 주문 수정 시 마지막 요청 승리
- **Implementation**: 별도 잠금 없이 UPDATE 실행
- **Trade-off**: 데이터 손실 가능성 있음
- **Exception**: 없음

### BR-CONC-002: No Optimistic Locking
- **Rule**: 버전 체크 없음
- **Rationale**: 단순성 우선, 소규모 매장 환경
- **Exception**: 없음

---

## 9. Error Response Rules

### BR-ERROR-001: Simple Error Format
- **Rule**: 에러 응답은 간단한 문자열 메시지
- **Format**: 
  ```json
  {
    "error": "Error message"
  }
  ```
- **Exception**: 없음

### BR-ERROR-002: HTTP Status Codes
- **Rule**: 적절한 HTTP 상태 코드 사용
- **Mapping**:
  - 400: Bad Request (잘못된 요청, 비즈니스 규칙 위반)
  - 401: Unauthorized (인증 실패, 토큰 만료)
  - 403: Forbidden (계정 잠금, 권한 없음)
  - 404: Not Found (리소스 없음)
  - 500: Internal Server Error (서버 에러)
- **Exception**: 없음

---

## 10. Data Validation Rules

### BR-VALID-001: Input Validation
- **Rule**: 모든 입력 데이터 검증 필수
- **Implementation**: @Valid 어노테이션 사용
- **Exception**: 없음

### BR-VALID-002: Required Fields
- **Rule**: 필수 필드 누락 시 에러 반환
- **HTTP Status**: 400 Bad Request
- **Error Message**: "Required field missing: {fieldName}"
- **Exception**: 없음

### BR-VALID-003: Data Type Validation
- **Rule**: 데이터 타입 불일치 시 에러 반환
- **HTTP Status**: 400 Bad Request
- **Error Message**: "Invalid data type for field: {fieldName}"
- **Exception**: 없음

---

## 11. Performance Rules

### BR-PERF-001: Current Session Query Only
- **Rule**: 주문 목록 조회 시 현재 세션 주문만 조회
- **Implementation**: 
  ```sql
  WHERE sessionId IN (
    SELECT id FROM table_session WHERE isActive = true
  )
  ```
- **Benefit**: 쿼리 성능 향상
- **Exception**: 없음

### BR-PERF-002: Index Usage
- **Rule**: 자주 조회되는 컬럼에 인덱스 생성
- **Indexes**:
  - `orders.sessionId`
  - `orders.status`
  - `table_session.tableId, isActive`
- **Exception**: 없음

---

## 12. Security Rules

### BR-SEC-001: Password Hashing
- **Rule**: 비밀번호는 bcrypt로 해싱 저장
- **Implementation**: BCryptPasswordEncoder
- **Exception**: 없음

### BR-SEC-002: JWT Secret Key
- **Rule**: JWT 서명에 강력한 비밀 키 사용
- **Implementation**: application.yml에서 설정
- **Exception**: 없음

### BR-SEC-003: HTTPS Requirement
- **Rule**: 프로덕션 환경에서 HTTPS 필수
- **Rationale**: JWT 토큰 보호
- **Exception**: 개발 환경은 HTTP 허용

---

## Rule Summary Table

| Rule ID | Category | Description | Priority |
|---------|----------|-------------|----------|
| BR-AUTH-001 | Authentication | 비밀번호 검증 실패 시 시도 횟수 증가 | High |
| BR-AUTH-002 | Authentication | 5회 실패 시 계정 30분 잠금 | High |
| BR-ORDER-001 | Order | 순차적 상태 전환만 허용 | High |
| BR-ORDER-002 | Order | 취소는 언제든 가능 | High |
| BR-DELETE-001 | Order | 완료/취소 상태만 삭제 가능 | High |
| BR-SESSION-002 | Session | 세션 종료 전 미완료 주문 확인 | High |
| BR-SSE-002 | SSE | 새 주문 생성 시에만 이벤트 발행 | Medium |
| BR-CONC-001 | Concurrency | Last-write-wins 전략 | Medium |
| BR-ERROR-001 | Error | 간단한 에러 메시지 형식 | Low |
| BR-PERF-001 | Performance | 현재 세션 주문만 조회 | Medium |
