# Domain Entities - Unit 1 (Frontend)

## Overview

Frontend에서 사용하는 도메인 엔티티를 TypeScript interface로 정의합니다.

---

## 1. Menu Domain

### Menu
```typescript
interface Menu {
  menuId: number;
  menuName: string;
  price: number;
  description: string;
  imageUrl: string;
  categoryId: number;
  storeId: number;
}
```

**설명**: 메뉴 정보

**사용처**:
- MenuPage: 메뉴 목록 표시
- MenuCard: 개별 메뉴 카드
- CartItem: 장바구니 항목 (Menu 객체 포함)

---

### Category
```typescript
interface Category {
  categoryId: number;
  categoryName: string;
  storeId: number;
}
```

**설명**: 메뉴 카테고리

**사용처**:
- MenuPage: 카테고리 필터링
- MenuManagementPage: 카테고리별 메뉴 조회

---

## 2. Cart Domain

### CartItem
```typescript
interface CartItem {
  menu: Menu;      // 전체 Menu 객체
  quantity: number;
}
```

**설명**: 장바구니 항목

**사용처**:
- CartPage: 장바구니 목록 표시
- useCart hook: 장바구니 상태 관리

---

### CartState
```typescript
interface CartState {
  items: CartItem[];
  totalAmount: number;
  
  // Actions
  addToCart: (menu: Menu, quantity: number) => void;
  updateQuantity: (menuId: number, quantity: number) => void;
  removeItem: (menuId: number) => void;
  clearCart: () => void;
}
```

**설명**: 장바구니 전역 상태 (Zustand store)

**사용처**:
- useCart hook
- CartPage
- MenuPage (메뉴 추가 시)

---

## 3. Order Domain

### Order
```typescript
interface Order {
  orderId: number;
  orderNumber: string;
  tableId: number;
  sessionId: string;
  totalAmount: number;
  status: OrderStatus;
  orderTime: string;  // ISO 8601 format
  items: OrderItem[];
}
```

**설명**: 주문 정보

**사용처**:
- OrderHistoryPage: 주문 내역 표시
- AdminDashboardPage: 실시간 주문 모니터링
- OrderCard: 개별 주문 카드

---

### OrderItem
```typescript
interface OrderItem {
  orderItemId: number;
  orderId: number;
  menuId: number;
  menuName: string;
  quantity: number;
  unitPrice: number;
}
```

**설명**: 주문 항목

**사용처**:
- Order 객체 내부
- OrderCard: 주문 상세 정보 표시

---

### OrderStatus
```typescript
enum OrderStatus {
  PENDING = 'PENDING',       // 대기중
  PREPARING = 'PREPARING',   // 준비중
  COMPLETED = 'COMPLETED',   // 완료
  CANCELLED = 'CANCELLED'    // 취소됨
}
```

**설명**: 주문 상태

**사용처**:
- Order 객체
- OrderCard: 상태 표시
- AdminDashboardPage: 상태 변경

---

### OrderRequestDto
```typescript
interface OrderRequestDto {
  tableId: number;
  sessionId: string;
  items: OrderItemRequestDto[];
  totalAmount: number;
}
```

**설명**: 주문 생성 요청 DTO

**사용처**:
- useOrder hook: createOrder()
- CartPage: 주문 확정 시

---

### OrderItemRequestDto
```typescript
interface OrderItemRequestDto {
  menuId: number;
  quantity: number;
  unitPrice: number;
}
```

**설명**: 주문 항목 요청 DTO

**사용처**:
- OrderRequestDto 내부

---

### OrderResponseDto
```typescript
interface OrderResponseDto {
  orderId: number;
  orderNumber: string;
  tableId: number;
  sessionId: string;
  totalAmount: number;
  status: OrderStatus;
  orderTime: string;
  items: OrderItem[];
}
```

**설명**: 주문 응답 DTO (Order와 동일)

**사용처**:
- API 응답 파싱

---

## 4. Table Domain

### Table
```typescript
interface Table {
  tableId: number;
  tableNumber: string;
  storeId: number;
}
```

**설명**: 테이블 정보

**사용처**:
- TableLoginPage: 테이블 로그인
- AdminDashboardPage: 테이블별 주문 그룹핑

---

### TableSession
```typescript
interface TableSession {
  sessionId: string;
  tableId: number;
  startTime: string;  // ISO 8601 format
  endTime: string | null;
  isActive: boolean;
}
```

**설명**: 테이블 세션 정보

**사용처**:
- 주문 생성 시 sessionId 사용
- 테이블 관리 (세션 종료)

---

### TableWithOrders (Admin 전용)
```typescript
interface TableWithOrders {
  tableId: number;
  tableNumber: string;
  totalAmount: number;
  orders: Order[];
}
```

**설명**: 테이블별 주문 그룹핑 (Backend에서 제공)

**사용처**:
- AdminDashboardPage: 테이블 카드 표시
- useOrderStream hook

---

## 5. Auth Domain

### TableLoginRequestDto
```typescript
interface TableLoginRequestDto {
  storeId: number;
  tableNumber: string;
  password: string;
}
```

**설명**: 테이블 로그인 요청 DTO

**사용처**:
- TableLoginPage
- useAuth hook: login()

---

### TableLoginResponseDto
```typescript
interface TableLoginResponseDto {
  tableId: number;
  sessionId: string;
  storeId: number;
  tableNumber: string;
}
```

**설명**: 테이블 로그인 응답 DTO

**사용처**:
- useAuth hook: login()
- SessionStorage 저장

---

### AdminLoginRequestDto
```typescript
interface AdminLoginRequestDto {
  storeId: number;
  username: string;
  password: string;
}
```

**설명**: 관리자 로그인 요청 DTO

**사용처**:
- AdminLoginPage
- useAuth hook: login()

---

### AdminLoginResponseDto
```typescript
interface AdminLoginResponseDto {
  token: string;
  refreshToken: string;
  adminId: number;
  username: string;
  storeId: number;
}
```

**설명**: 관리자 로그인 응답 DTO

**사용처**:
- useAuth hook: login()
- SessionStorage 저장 (token, refreshToken)

---

### User (Auth State)
```typescript
interface User {
  userId: number;
  username: string;
  role: 'customer' | 'admin';
  storeId: number;
}
```

**설명**: 인증된 사용자 정보

**사용처**:
- useAuth hook
- Header 컴포넌트 (사용자 정보 표시)

---

### AuthState
```typescript
interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  token: string | null;
  
  // Actions
  login: (credentials: TableLoginRequestDto | AdminLoginRequestDto) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
}
```

**설명**: 인증 전역 상태 (Zustand store)

**사용처**:
- useAuth hook
- Axios interceptor (토큰 관리)

---

## 6. Admin Domain

### Admin
```typescript
interface Admin {
  adminId: number;
  username: string;
  storeId: number;
}
```

**설명**: 관리자 정보

**사용처**:
- AdminLoginPage
- Header (관리자 이름 표시)

---

### OrderStatusUpdateDto
```typescript
interface OrderStatusUpdateDto {
  status: OrderStatus;
}
```

**설명**: 주문 상태 변경 요청 DTO

**사용처**:
- OrderDetailModal: 상태 변경 시
- useOrder hook: updateOrderStatus()

---

## 7. Store Domain

### Store
```typescript
interface Store {
  storeId: number;
  storeName: string;
  storeCode: string;
}
```

**설명**: 매장 정보

**사용처**:
- 로그인 시 storeId 사용

---

## 8. Error Domain

### ErrorResponseDto
```typescript
interface ErrorResponseDto {
  status: number;
  message: string;
  timestamp: string;
  path: string;
}
```

**설명**: Backend 에러 응답 DTO

**사용처**:
- Axios interceptor: 에러 처리
- GlobalExceptionHandler 응답 파싱

---

### ApiError
```typescript
interface ApiError {
  message: string;
  status: number;
  data?: any;
}
```

**설명**: Frontend 에러 객체

**사용처**:
- useQuery, useMutation 에러 상태
- 에러 메시지 표시

---

## 9. UI State Domain

### LoadingState
```typescript
interface LoadingState {
  isLoading: boolean;
  message?: string;
}
```

**설명**: 로딩 상태

**사용처**:
- LoadingSpinner
- 각 페이지의 로딩 상태 관리

---

### ModalState
```typescript
interface ModalState {
  isOpen: boolean;
  title: string;
  message: string;
  onConfirm?: () => void;
  onCancel?: () => void;
}
```

**설명**: Modal 다이얼로그 상태

**사용처**:
- ConfirmDialog
- 에러 Modal
- 확인 Modal

---

### ToastState
```typescript
interface ToastState {
  isVisible: boolean;
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
  duration: number;  // milliseconds
}
```

**설명**: Toast 알림 상태

**사용처**:
- Toast 컴포넌트
- 성공/에러 알림

---

## 10. SSE Domain

### SseEvent
```typescript
interface SseEvent {
  type: 'new-order' | 'order-status-change' | 'order-deleted';
  data: any;
}
```

**설명**: SSE 이벤트

**사용처**:
- useOrderStream hook
- EventSource 이벤트 핸들러

---

### NewOrderEvent
```typescript
interface NewOrderEvent {
  type: 'new-order';
  data: Order;
}
```

**설명**: 신규 주문 SSE 이벤트

**사용처**:
- useOrderStream hook: 신규 주문 추가

---

### OrderStatusChangeEvent
```typescript
interface OrderStatusChangeEvent {
  type: 'order-status-change';
  data: {
    orderId: number;
    newStatus: OrderStatus;
  };
}
```

**설명**: 주문 상태 변경 SSE 이벤트

**사용처**:
- useOrderStream hook: 주문 상태 업데이트

---

### OrderDeletedEvent
```typescript
interface OrderDeletedEvent {
  type: 'order-deleted';
  data: {
    orderId: number;
  };
}
```

**설명**: 주문 삭제 SSE 이벤트

**사용처**:
- useOrderStream hook: 주문 제거

---

## Entity Relationships

```
Store
  ├── Table (1:N)
  ├── Menu (1:N)
  ├── Category (1:N)
  └── Admin (1:N)

Table
  ├── TableSession (1:N)
  └── Order (1:N)

TableSession
  └── Order (1:N)

Menu
  ├── Category (N:1)
  └── OrderItem (1:N)

Order
  ├── Table (N:1)
  ├── TableSession (N:1)
  └── OrderItem (1:N)

OrderItem
  ├── Order (N:1)
  └── Menu (N:1)

CartItem
  └── Menu (N:1)
```

---

## Type Guards

### isOrder
```typescript
function isOrder(obj: any): obj is Order {
  return (
    typeof obj === 'object' &&
    typeof obj.orderId === 'number' &&
    typeof obj.orderNumber === 'string' &&
    typeof obj.status === 'string'
  );
}
```

### isApiError
```typescript
function isApiError(error: any): error is ApiError {
  return (
    typeof error === 'object' &&
    typeof error.message === 'string' &&
    typeof error.status === 'number'
  );
}
```

---

## Notes

- 모든 interface는 TypeScript로 정의
- Backend API 응답과 일치하도록 설계
- Zustand store는 state + actions를 포함하는 interface
- DTO는 API 요청/응답 전용
- Domain entity는 비즈니스 로직에서 사용
