# Business Logic Model - Unit 3 (Backend Admin API)

## Overview
Unit 3의 비즈니스 로직 흐름 및 처리 모델

---

## 1. Admin Authentication Flow

### 1.1 Login Process
```
[Client] POST /api/admin/login
    ↓
[AdminController] 요청 수신
    ↓
[AdminService.login(username, password)]
    ↓
[1] Admin 조회 (username)
    ├─ 존재하지 않음 → 에러 반환
    └─ 존재함 → 다음 단계
    ↓
[2] 계정 잠금 확인
    ├─ lockedUntil > now → 에러 반환 (계정 잠금)
    └─ 잠금 해제 → 다음 단계
    ↓
[3] 비밀번호 검증
    ├─ 실패 → loginAttempts++
    │   ├─ loginAttempts >= 5 → 계정 잠금 (30분)
    │   └─ 에러 반환
    └─ 성공 → loginAttempts = 0
    ↓
[4] JWT 토큰 생성 (16시간 만료)
    ↓
[5] 토큰 반환
```

### 1.2 Account Locking Logic
- **Trigger**: 5회 연속 로그인 실패
- **Duration**: 30분
- **Implementation**:
  ```
  lockedUntil = now + 30 minutes
  loginAttempts = 5
  ```
- **Unlock**: lockedUntil 시각 이후 자동 해제

---

## 2. Order Management Flow

### 2.1 Order List Query
```
[Client] GET /api/admin/orders
    ↓
[AdminController] 요청 수신
    ↓
[AdminService.getOrders(storeId)]
    ↓
[OrderMapper] 현재 세션 주문만 조회
    - WHERE sessionId IN (SELECT id FROM table_session WHERE isActive = true)
    - ORDER BY createdAt DESC
    ↓
[OrderDTO 변환] Order → OrderResponseDto
    ↓
[응답 반환]
```

### 2.2 Order Status Change
```
[Client] PATCH /api/admin/orders/{orderId}/status
    ↓
[AdminController] 요청 수신
    ↓
[AdminService.updateOrderStatus(orderId, newStatus)]
    ↓
[1] Order 조회
    ├─ 존재하지 않음 → 에러 반환
    └─ 존재함 → 다음 단계
    ↓
[2] 상태 전환 검증
    ├─ PENDING → PREPARING (허용)
    ├─ PREPARING → COMPLETED (허용)
    ├─ ANY → CANCELLED (허용)
    └─ 기타 → 에러 반환 (Invalid transition)
    ↓
[3] 상태 업데이트
    ↓
[4] 응답 반환
```

### 2.3 Order Deletion
```
[Client] DELETE /api/admin/orders/{orderId}
    ↓
[AdminController] 요청 수신
    ↓
[AdminService.deleteOrder(orderId)]
    ↓
[1] Order 조회
    ├─ 존재하지 않음 → 에러 반환
    └─ 존재함 → 다음 단계
    ↓
[2] 삭제 가능 상태 확인
    ├─ status IN (COMPLETED, CANCELLED) → 허용
    └─ status IN (PENDING, PREPARING) → 에러 반환
    ↓
[3] OrderItem 삭제 (CASCADE)
    ↓
[4] Order 삭제 (Hard delete)
    ↓
[5] 응답 반환
```

---

## 3. Table Session Management Flow

### 3.1 Session End Process
```
[Client] POST /api/admin/tables/{tableId}/end-session
    ↓
[AdminController] 요청 수신
    ↓
[TableSessionService.endSession(tableId)]
    ↓
[1] 활성 세션 조회
    - WHERE tableId = ? AND isActive = true
    ├─ 존재하지 않음 → 에러 반환
    └─ 존재함 → 다음 단계
    ↓
[2] 미완료 주문 확인
    - WHERE sessionId = ? AND status IN (PENDING, PREPARING)
    ├─ 존재함 → 에러 반환 (미완료 주문 있음)
    └─ 없음 → 다음 단계
    ↓
[3] 세션 종료
    - endedAt = now
    - isActive = false
    ↓
[4] 응답 반환
```

### 3.2 Past Orders Query
```
[Client] GET /api/admin/tables/{tableId}/past-orders
    ↓
[AdminController] 요청 수신
    ↓
[TableSessionService.getPastOrders(tableId, page, size)]
    ↓
[1] 종료된 세션 조회
    - WHERE tableId = ? AND isActive = false
    - ORDER BY endedAt DESC
    ↓
[2] 각 세션의 주문 조회
    - WHERE sessionId IN (...)
    ↓
[3] 페이지네이션 적용
    ↓
[4] DTO 변환 및 응답 반환
```

---

## 4. SSE Event Publishing Flow

### 4.1 SSE Connection Establishment
```
[Client] GET /api/admin/orders/stream
    ↓
[AdminController] SSE 연결 요청
    ↓
[SseEmitterManager.createEmitter(adminId)]
    ↓
[1] SseEmitter 생성 (무제한 타임아웃)
    ↓
[2] Emitter 저장 (Map<adminId, SseEmitter>)
    ↓
[3] 연결 성공 이벤트 전송
    ↓
[4] Emitter 반환
```

### 4.2 New Order Event Publishing
```
[Customer API] 새 주문 생성
    ↓
[OrderService.publishNewOrderEvent(order)]
    ↓
[SseEmitterManager.broadcast(event)]
    ↓
[1] 모든 활성 Emitter 조회
    ↓
[2] 각 Emitter에 이벤트 전송
    - event: "new-order"
    - data: OrderResponseDto (JSON)
    ↓
[3] 전송 실패 시 Emitter 제거
```

### 4.3 SSE Connection Cleanup
```
[Emitter 타임아웃 또는 에러]
    ↓
[SseEmitterManager.removeEmitter(adminId)]
    ↓
[1] Emitter 제거
    ↓
[2] 리소스 정리
```

---

## 5. Data Flow Summary

### 5.1 Admin Login
```
Client → AdminController → AdminService → AdminMapper → Database
                                ↓
                         JwtTokenProvider
                                ↓
                            JWT Token
```

### 5.2 Order Management
```
Client → AdminController → AdminService → OrderMapper → Database
                                ↓
                         Business Rules
                                ↓
                            Response
```

### 5.3 SSE Real-time Updates
```
Customer API → OrderService → SseEmitterManager → All Connected Clients
                                    ↓
                            Broadcast Event
```

---

## 6. Error Handling Strategy

### 6.1 Error Response Format
```json
{
  "error": "Error message string"
}
```

### 6.2 Common Error Scenarios
| Scenario | HTTP Status | Error Message |
|----------|-------------|---------------|
| 인증 실패 | 401 | "Invalid username or password" |
| 계정 잠금 | 403 | "Account locked. Try again after 30 minutes" |
| JWT 만료 | 401 | "Token expired" |
| 권한 없음 | 403 | "Access denied" |
| 리소스 없음 | 404 | "Resource not found" |
| 잘못된 상태 전환 | 400 | "Invalid status transition" |
| 미완료 주문 존재 | 400 | "Cannot end session with pending orders" |
| 삭제 불가 상태 | 400 | "Cannot delete order in current status" |

---

## 7. Concurrency Handling

### 7.1 Order Modification
- **Strategy**: Last-write-wins
- **Implementation**: 마지막 요청이 승리
- **Trade-off**: 간단하지만 데이터 손실 가능성

### 7.2 Session End
- **Strategy**: Pessimistic approach
- **Implementation**: 미완료 주문 확인 후 에러 반환
- **Trade-off**: 안전하지만 사용자 개입 필요

---

## 8. Performance Considerations

### 8.1 Order List Query Optimization
- **Strategy**: 현재 세션 주문만 조회
- **Implementation**: 
  ```sql
  WHERE sessionId IN (
    SELECT id FROM table_session WHERE isActive = true
  )
  ```
- **Benefit**: 과거 주문 제외로 쿼리 성능 향상

### 8.2 SSE Connection Management
- **Strategy**: 무제한 연결 유지
- **Implementation**: 타임아웃 없음
- **Trade-off**: 메모리 사용 증가 가능성

---

## 9. Security Considerations

### 9.1 Authentication
- bcrypt 비밀번호 해싱
- 5회 로그인 실패 시 계정 잠금
- JWT 토큰 16시간 만료

### 9.2 Authorization
- 단일 관리자 모델 (권한 구분 없음)
- JWT 토큰 검증 필터

### 9.3 Input Validation
- @Valid 어노테이션 사용
- DTO 레벨 검증
