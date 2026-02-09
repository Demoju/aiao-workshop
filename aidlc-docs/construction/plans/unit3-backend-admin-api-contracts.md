# Contract/Interface Definition for Unit 3 (Backend Admin API)

## Unit Context
- **Stories**: 관리자 인증, 주문 관리, 테이블 세션 관리, SSE 실시간 업데이트
- **Dependencies**: PostgreSQL Database, Customer API (주문 생성 이벤트)
- **Database Entities**: Admin, Order, OrderItem, TableSession, Table, Store

---

## Business Logic Layer

### AdminService
- `login(username: String, password: String) -> AdminLoginResponseDto`: 관리자 로그인
  - Args: username (사용자명), password (비밀번호)
  - Returns: AdminLoginResponseDto (JWT 토큰 포함)
  - Raises: AuthenticationException (인증 실패), AccountLockedException (계정 잠금)

- `getOrders(storeId: Long) -> List<OrderResponseDto>`: 현재 세션 주문 목록 조회
  - Args: storeId (매장 ID)
  - Returns: List<OrderResponseDto> (주문 목록)
  - Raises: None

- `updateOrderStatus(orderId: Long, newStatus: OrderStatus) -> void`: 주문 상태 변경
  - Args: orderId (주문 ID), newStatus (새 상태)
  - Returns: void
  - Raises: OrderNotFoundException, InvalidStatusTransitionException

- `deleteOrder(orderId: Long) -> void`: 주문 삭제
  - Args: orderId (주문 ID)
  - Returns: void
  - Raises: OrderNotFoundException, InvalidOrderStatusException

### TableSessionService
- `endSession(tableId: Long) -> void`: 테이블 세션 종료
  - Args: tableId (테이블 ID)
  - Returns: void
  - Raises: SessionNotFoundException, UncompletedOrdersException

- `getPastOrders(tableId: Long, page: int, size: int) -> Page<OrderResponseDto>`: 과거 주문 내역 조회
  - Args: tableId (테이블 ID), page (페이지 번호), size (페이지 크기)
  - Returns: Page<OrderResponseDto> (페이지네이션된 주문 목록)
  - Raises: None

### OrderEventPublisher
- `publishNewOrderEvent(order: Order) -> void`: 새 주문 이벤트 발행
  - Args: order (주문 엔티티)
  - Returns: void
  - Raises: None

### SseEmitterManager
- `createEmitter(adminId: String) -> SseEmitter`: SSE 연결 생성
  - Args: adminId (관리자 ID)
  - Returns: SseEmitter (SSE 연결 객체)
  - Raises: None

- `broadcast(event: OrderEvent) -> void`: 모든 연결에 이벤트 브로드캐스트
  - Args: event (주문 이벤트)
  - Returns: void
  - Raises: None

- `removeEmitter(adminId: String) -> void`: SSE 연결 제거
  - Args: adminId (관리자 ID)
  - Returns: void
  - Raises: None

---

## API Layer

### AdminController

#### POST /api/admin/login
- **Description**: 관리자 로그인
- **Request Body**: AdminLoginRequestDto (username, password)
- **Response**: AdminLoginResponseDto (token)
- **Status Codes**: 200 (성공), 401 (인증 실패), 403 (계정 잠금)

#### GET /api/admin/orders
- **Description**: 주문 목록 조회
- **Headers**: Authorization: Bearer {token}
- **Response**: List<OrderResponseDto>
- **Status Codes**: 200 (성공), 401 (인증 실패)

#### PATCH /api/admin/orders/{orderId}/status
- **Description**: 주문 상태 변경
- **Headers**: Authorization: Bearer {token}
- **Path Params**: orderId (주문 ID)
- **Request Body**: OrderStatusUpdateDto (status)
- **Response**: void
- **Status Codes**: 200 (성공), 400 (잘못된 전환), 404 (주문 없음)

#### DELETE /api/admin/orders/{orderId}
- **Description**: 주문 삭제
- **Headers**: Authorization: Bearer {token}
- **Path Params**: orderId (주문 ID)
- **Response**: void
- **Status Codes**: 200 (성공), 400 (삭제 불가 상태), 404 (주문 없음)

#### POST /api/admin/tables/{tableId}/end-session
- **Description**: 테이블 세션 종료
- **Headers**: Authorization: Bearer {token}
- **Path Params**: tableId (테이블 ID)
- **Response**: void
- **Status Codes**: 200 (성공), 400 (미완료 주문 존재), 404 (세션 없음)

#### GET /api/admin/tables/{tableId}/past-orders
- **Description**: 과거 주문 내역 조회
- **Headers**: Authorization: Bearer {token}
- **Path Params**: tableId (테이블 ID)
- **Query Params**: page (페이지 번호), size (페이지 크기)
- **Response**: Page<OrderResponseDto>
- **Status Codes**: 200 (성공), 401 (인증 실패)

#### GET /api/admin/orders/stream
- **Description**: SSE 실시간 주문 업데이트
- **Headers**: Authorization: Bearer {token}
- **Response**: SSE Stream (OrderEvent)
- **Status Codes**: 200 (성공), 401 (인증 실패)

---

## Repository Layer

### AdminMapper
- `findByUsername(username: String) -> Optional<Admin>`: 사용자명으로 관리자 조회
  - Args: username (사용자명)
  - Returns: Optional<Admin> (관리자 엔티티)
  - Raises: None

- `updateLoginAttempts(adminId: Long, attempts: int, lockedUntil: Timestamp) -> void`: 로그인 시도 횟수 업데이트
  - Args: adminId (관리자 ID), attempts (시도 횟수), lockedUntil (잠금 해제 시각)
  - Returns: void
  - Raises: None

### OrderMapper
- `findBySessionIdIn(sessionIds: List<Long>) -> List<Order>`: 세션 ID 목록으로 주문 조회
  - Args: sessionIds (세션 ID 목록)
  - Returns: List<Order> (주문 목록)
  - Raises: None

- `findById(orderId: Long) -> Optional<Order>`: 주문 ID로 조회
  - Args: orderId (주문 ID)
  - Returns: Optional<Order> (주문 엔티티)
  - Raises: None

- `updateStatus(orderId: Long, status: OrderStatus) -> void`: 주문 상태 업데이트
  - Args: orderId (주문 ID), status (새 상태)
  - Returns: void
  - Raises: None

- `deleteById(orderId: Long) -> void`: 주문 삭제
  - Args: orderId (주문 ID)
  - Returns: void
  - Raises: None

### TableSessionMapper
- `findActiveByTableId(tableId: Long) -> Optional<TableSession>`: 활성 세션 조회
  - Args: tableId (테이블 ID)
  - Returns: Optional<TableSession> (세션 엔티티)
  - Raises: None

- `endSession(sessionId: Long, endedAt: Timestamp) -> void`: 세션 종료
  - Args: sessionId (세션 ID), endedAt (종료 시각)
  - Returns: void
  - Raises: None

- `findInactiveByTableId(tableId: Long, page: int, size: int) -> List<TableSession>`: 종료된 세션 조회
  - Args: tableId (테이블 ID), page (페이지), size (크기)
  - Returns: List<TableSession> (세션 목록)
  - Raises: None

---

## Security Components

### JwtTokenProvider
- `generateToken(admin: Admin) -> String`: JWT 토큰 생성
  - Args: admin (관리자 엔티티)
  - Returns: String (JWT 토큰)
  - Raises: None

- `validateToken(token: String) -> boolean`: JWT 토큰 검증
  - Args: token (JWT 토큰)
  - Returns: boolean (유효 여부)
  - Raises: None

- `getAdminIdFromToken(token: String) -> Long`: 토큰에서 관리자 ID 추출
  - Args: token (JWT 토큰)
  - Returns: Long (관리자 ID)
  - Raises: InvalidTokenException

### JwtAuthenticationFilter
- `doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) -> void`: JWT 검증 필터
  - Args: request, response, filterChain
  - Returns: void
  - Raises: None
