# Business Logic Model - Unit 1 (Frontend)

## Overview

Frontend의 핵심 비즈니스 로직을 정의합니다. 이 문서는 기술 구현과 무관하게 비즈니스 흐름과 규칙을 설명합니다.

---

## 1. 장바구니 관리 로직

### 1.1 장바구니 데이터 구조

```typescript
interface CartItem {
  menu: Menu;      // 전체 Menu 객체 저장
  quantity: number;
}

// Zustand store
interface CartState {
  items: CartItem[];
  totalAmount: number;
}
```

**설계 근거**: 전체 Menu 객체를 저장하여 장바구니 화면에서 추가 API 호출 없이 메뉴 정보 표시 가능

### 1.2 메뉴 추가 로직

```
Input: Menu 객체, quantity
Process:
  1. items 배열에서 동일한 menuId 검색
  2. IF 존재:
       기존 항목의 quantity 증가
     ELSE:
       새 CartItem 생성하여 배열에 추가
  3. totalAmount 재계산
Output: 업데이트된 CartState
```

**비즈니스 규칙**:
- 동일 메뉴 중복 추가 시 수량만 증가
- 메뉴당 최대 수량: 99개
- 수량 초과 시 에러 메시지 표시

### 1.3 수량 조절 로직

```
Input: menuId, newQuantity
Process:
  1. items 배열에서 menuId로 항목 찾기
  2. IF newQuantity === 0:
       항목 삭제
     ELSE IF newQuantity > 99:
       에러 (최대 수량 초과)
     ELSE:
       quantity 업데이트
  3. totalAmount 재계산
Output: 업데이트된 CartState
```

### 1.4 장바구니 비우기 로직

```
Process:
  1. items 배열 초기화 ([])
  2. totalAmount = 0
  3. LocalStorage 업데이트
```

### 1.5 장바구니 지속성

```
Storage: LocalStorage
Key: "cart"
Format: JSON.stringify(CartState)

On page load:
  1. LocalStorage에서 "cart" 읽기
  2. IF 존재:
       JSON.parse하여 Zustand store 초기화
     ELSE:
       빈 장바구니로 초기화
```

---

## 2. 주문 생성 로직

### 2.1 주문 생성 플로우

```
Input: CartState
Process:
  1. Frontend 검증:
     - 장바구니가 비어있지 않은지 확인
     - 각 항목의 quantity > 0 확인
  2. IF 검증 실패:
       에러 메시지 표시 및 중단
  3. SessionStorage에서 tableId, sessionId 읽기
  4. OrderRequestDto 생성:
       {
         tableId,
         sessionId,
         items: CartState.items.map(item => ({
           menuId: item.menu.menuId,
           quantity: item.quantity,
           unitPrice: item.menu.price
         })),
         totalAmount: CartState.totalAmount
       }
  5. POST /api/customer/orders 호출
  6. IF 성공:
       - 장바구니 비우기
       - Toast 알림 표시 (2초): "주문이 완료되었습니다"
       - 즉시 메뉴 화면으로 이동
     ELSE IF 네트워크 에러:
       - 장바구니 유지
       - Modal 에러 메시지 표시
       - "재시도" 버튼 제공
     ELSE:
       - 장바구니 유지
       - 에러 메시지 표시
```

### 2.2 API 재시도 로직

```
Max retries: 3
Retry strategy: Exponential backoff

Process:
  1. 첫 번째 시도
  2. IF 실패 AND retries < 3:
       대기 (2^retries 초)
       재시도
  3. IF 3회 모두 실패:
       Toast 표시: "주문 생성에 실패했습니다. 다시 시도해주세요."
```

---

## 3. 주문 내역 조회 로직

### 3.1 주문 목록 로드

```
Input: tableId, sessionId (from SessionStorage)
Process:
  1. GET /api/customer/orders?tableId={tableId}&sessionId={sessionId}
  2. IF 성공:
       - orders 배열을 Zustand store에 저장
       - 시간 역순 정렬 (최신 주문이 위)
     ELSE:
       - 에러 메시지 표시
  3. IF orders.length === 0:
       "주문 내역이 없습니다" 메시지 표시
```

### 3.2 주문 상태 표시

```
Order status mapping:
  - PENDING: "대기중"
  - PREPARING: "준비중"
  - COMPLETED: "완료"
  - CANCELLED: "취소됨"
```

---

## 4. 주문 취소 로직

### 4.1 취소 가능 여부 확인

```
Input: orderId
Process:
  1. Backend API 호출하여 취소 가능 여부 확인
     (주문 수락 전인지 Backend에서 검증)
  2. IF 취소 가능:
       "주문을 취소하시겠습니까?" 확인 다이얼로그 표시
     ELSE:
       "이미 수락된 주문은 취소할 수 없습니다" 메시지 표시
```

### 4.2 주문 취소 실행

```
Input: orderId
Process:
  1. 사용자 확인 후 DELETE /api/customer/orders/{orderId} 호출
  2. IF 성공:
       - orders 배열에서 해당 주문 제거
       - Toast 표시: "주문이 취소되었습니다"
     ELSE:
       - Modal 에러 메시지 표시
```

---

## 5. 실시간 주문 모니터링 로직 (Admin)

### 5.1 SSE 연결 및 초기 데이터 로드

```
On AdminDashboardPage mount:
  1. REST API로 초기 주문 목록 로드:
     GET /api/admin/orders
  2. Backend에서 테이블별로 그룹핑된 데이터 수신:
     {
       tables: [
         {
           tableId: 1,
           tableNumber: "1",
           totalAmount: 45000,
           orders: [...]
         },
         ...
       ]
     }
  3. Zustand store에 저장
  4. SSE 연결 시작:
     EventSource('/api/admin/orders/stream')
```

### 5.2 SSE 이벤트 처리

```
Event: "new-order"
Data: Order 객체
Process:
  1. 해당 테이블의 orders 배열 맨 위에 추가
  2. 테이블 totalAmount 업데이트
  3. 애니메이션 효과 적용 (fade-in, highlight)
  4. 사운드 알림 (optional)

Event: "order-status-change"
Data: { orderId, newStatus }
Process:
  1. orders 배열에서 orderId로 주문 찾기
  2. status 업데이트
  3. UI 반영

Event: "order-deleted"
Data: { orderId }
Process:
  1. orders 배열에서 orderId로 주문 찾기
  2. 배열에서 제거
  3. 테이블 totalAmount 재계산
  4. UI 반영
```

### 5.3 SSE 재연결 로직

```
Reconnection strategy: Exponential backoff

On connection error:
  1. 연결 종료
  2. 대기 시간 계산: min(2^retries * 1000, 30000) ms
  3. 대기 후 재연결 시도
  4. IF 재연결 성공:
       retries = 0
       초기 데이터 다시 로드 (REST API)
```

---

## 6. 테이블 관리 로직 (Admin)

### 6.1 테이블 세션 종료

```
Input: tableId
Process:
  1. "테이블 이용을 완료하시겠습니까?" 확인 다이얼로그
  2. 사용자 확인 후 POST /api/admin/tables/{tableId}/end-session
  3. IF 성공:
       - 해당 테이블의 orders 배열 초기화
       - totalAmount = 0
       - Toast 표시: "테이블 세션이 종료되었습니다"
     ELSE:
       - Modal 에러 메시지 표시
```

### 6.2 주문 삭제 (직권)

```
Input: orderId
Process:
  1. "주문을 삭제하시겠습니까?" 확인 다이얼로그
  2. 사용자 확인 후 DELETE /api/admin/orders/{orderId}
  3. IF 성공:
       - orders 배열에서 해당 주문 제거
       - 테이블 totalAmount 재계산
       - Toast 표시: "주문이 삭제되었습니다"
     ELSE:
       - Modal 에러 메시지 표시
```

### 6.3 과거 주문 내역 조회

```
Input: tableId, startDate, endDate
Process:
  1. GET /api/admin/tables/{tableId}/past-orders?startDate={startDate}&endDate={endDate}
  2. IF 성공:
       - Modal에 과거 주문 목록 표시
       - 시간 역순 정렬
     ELSE:
       - 에러 메시지 표시
```

---

## 7. 인증 로직

### 7.1 테이블 로그인 (Customer)

```
Input: storeId, tableNumber, password
Process:
  1. POST /api/customer/login
     Body: { storeId, tableNumber, password }
  2. IF 성공:
       Response: { tableId, sessionId }
       - SessionStorage에 저장:
         - tableId
         - sessionId
         - storeId
         - tableNumber
       - 메뉴 화면으로 이동
     ELSE:
       - 에러 메시지 표시: "로그인 정보가 올바르지 않습니다"
```

### 7.2 관리자 로그인 (Admin)

```
Input: storeId, username, password
Process:
  1. POST /api/admin/login
     Body: { storeId, username, password }
  2. IF 성공:
       Response: { token, adminId, username }
       - SessionStorage에 저장:
         - jwt_token
         - adminId
         - username
       - Axios interceptor에 토큰 설정
       - Admin 대시보드로 이동
     ELSE:
       - 에러 메시지 표시: "로그인 정보가 올바르지 않습니다"
```

### 7.3 JWT 토큰 갱신

```
On 401 Unauthorized response:
  1. IF refresh token 존재:
       POST /api/admin/refresh
       Body: { refreshToken }
       IF 성공:
         - 새 토큰 SessionStorage에 저장
         - 원래 요청 재시도
       ELSE:
         - 자동 로그아웃
         - 로그인 페이지로 이동
     ELSE:
       - 자동 로그아웃
       - 로그인 페이지로 이동
```

---

## 8. 상태 복구 로직

### 8.1 페이지 새로고침 시

```
On app initialization:
  1. LocalStorage에서 장바구니 복구
  2. SessionStorage에서 로그인 정보 복구:
     - Customer: tableId, sessionId
     - Admin: jwt_token, adminId
  3. IF 로그인 정보 존재:
       - Axios interceptor에 토큰 설정
       - 인증 상태 복원
     ELSE:
       - 로그인 페이지로 이동
```

---

## 9. 에러 처리 로직

### 9.1 네트워크 에러

```
On network error:
  1. Modal 다이얼로그 표시:
     Title: "네트워크 오류"
     Message: "네트워크 연결을 확인해주세요"
     Buttons: ["재시도", "닫기"]
  2. IF "재시도" 클릭:
       원래 요청 재실행
```

### 9.2 ErrorBoundary

```
On React error:
  1. 에러 페이지 표시:
     - 에러 메시지
     - "홈으로 이동" 버튼
  2. 에러 로깅 (console.error)
```

---

## 10. UI 상태 관리

### 10.1 로딩 상태

```
각 API 호출 시:
  1. loading = true
  2. LoadingSpinner 표시
  3. API 완료 후 loading = false
```

### 10.2 에러 상태

```
API 에러 발생 시:
  1. error 객체 저장
  2. 에러 메시지 표시 (Modal 또는 Toast)
  3. 사용자 액션 후 error = null
```

---

## Notes

- 모든 비즈니스 로직은 기술 구현과 독립적으로 설계
- 상세 구현은 Code Generation 단계에서 진행
- Backend API 계약은 NFR Design 단계에서 정의
