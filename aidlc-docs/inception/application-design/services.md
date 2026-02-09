# Services - 테이블오더 서비스

## Service Layer Architecture

### Design Approach
- **Thin Services**: 단순 CRUD 중심, 비즈니스 로직 최소화
- **Transaction Management**: Service 레이어에서 트랜잭션 경계 관리
- **Orchestration**: 여러 Repository 호출 조율

---

## Backend Services

### CustomerService

#### Purpose
고객용 비즈니스 로직 처리

#### Responsibilities
- 메뉴 조회 로직
- 주문 생성 로직 (세션 시작 포함)
- 주문 내역 조회 로직 (현재 세션만)
- 주문 취소 로직 (수락 전 검증)
- 테이블 로그인 검증

#### Dependencies
- CustomerMapper (데이터 액세스)
- TableSessionService (세션 관리)
- OrderService (주문 처리)

#### Transaction Boundaries
- `createOrder()`: 트랜잭션 필요 (주문 생성 + 주문 항목 생성 + 세션 시작)
- `cancelOrder()`: 트랜잭션 필요 (주문 삭제 + 주문 항목 삭제)

#### Key Methods
```java
@Transactional(readOnly = true)
public List<MenuResponseDto> getMenus(Long categoryId)

@Transactional
public OrderResponseDto createOrder(OrderRequestDto orderRequest)

@Transactional(readOnly = true)
public List<OrderResponseDto> getOrders(Long tableId, String sessionId)

@Transactional
public void cancelOrder(Long orderId)

@Transactional(readOnly = true)
public TableLoginResponseDto login(TableLoginRequestDto loginRequest)
```

---

### AdminService

#### Purpose
관리자용 비즈니스 로직 처리

#### Responsibilities
- 관리자 인증 로직 (JWT 발급)
- 주문 목록 조회 로직
- 주문 상태 변경 로직
- 주문 삭제 로직
- 테이블 세션 종료 로직
- 과거 주문 내역 조회 로직

#### Dependencies
- AdminMapper (데이터 액세스)
- JwtTokenProvider (JWT 생성/검증)
- PasswordEncoder (비밀번호 검증)
- OrderService (주문 처리)
- TableSessionService (세션 관리)

#### Transaction Boundaries
- `login()`: 트랜잭션 필요 (관리자 조회 + JWT 발급)
- `updateOrderStatus()`: 트랜잭션 필요 (상태 변경 + SSE 이벤트 발행)
- `deleteOrder()`: 트랜잭션 필요 (주문 삭제 + 주문 항목 삭제)
- `endTableSession()`: 트랜잭션 필요 (세션 종료 + 주문 이력 이동)

#### Key Methods
```java
@Transactional
public AdminLoginResponseDto login(AdminLoginRequestDto loginRequest)

@Transactional(readOnly = true)
public List<OrderResponseDto> getOrders(Long tableId)

@Transactional
public void updateOrderStatus(Long orderId, OrderStatus status)

@Transactional
public void deleteOrder(Long orderId)

@Transactional
public void endTableSession(Long tableId)

@Transactional(readOnly = true)
public List<OrderResponseDto> getPastOrders(Long tableId, LocalDate startDate, LocalDate endDate)
```

---

### OrderService

#### Purpose
주문 관련 비즈니스 로직 및 SSE 이벤트 관리

#### Responsibilities
- 주문 생성 처리
- 주문 상태 관리
- SSE 이벤트 발행 (신규 주문, 상태 변경)
- 주문 검증
- 주문 취소 가능 여부 확인

#### Dependencies
- CustomerMapper (데이터 액세스)
- AdminMapper (데이터 액세스)
- SseEmitterManager (SSE 연결 관리)

#### SSE Event Publishing
- **신규 주문 이벤트**: 주문 생성 시 모든 관리자에게 알림
- **상태 변경 이벤트**: 주문 상태 변경 시 모든 관리자에게 알림

#### Transaction Boundaries
- `createOrder()`: 트랜잭션 필요 (주문 생성 + SSE 이벤트 발행)
- `updateOrderStatus()`: 트랜잭션 필요 (상태 변경 + SSE 이벤트 발행)

#### Key Methods
```java
@Transactional
public Order createOrder(OrderRequestDto orderRequest)

@Transactional
public void updateOrderStatus(Long orderId, OrderStatus status)

public void publishNewOrderEvent(Order order)

public void publishOrderStatusChangeEvent(Order order)

@Transactional(readOnly = true)
public boolean validateOrder(OrderRequestDto orderRequest)

@Transactional(readOnly = true)
public boolean canCancelOrder(Long orderId)
```

---

### MenuService

#### Purpose
메뉴 관련 비즈니스 로직 처리

#### Responsibilities
- 메뉴 조회 로직
- 카테고리 조회 로직
- 메뉴 가용성 검증

#### Dependencies
- CustomerMapper (데이터 액세스)

#### Transaction Boundaries
- 모든 메서드: `@Transactional(readOnly = true)` (조회만)

#### Key Methods
```java
@Transactional(readOnly = true)
public List<Menu> getMenus(Long categoryId)

@Transactional(readOnly = true)
public Menu getMenu(Long menuId)

@Transactional(readOnly = true)
public List<Category> getCategories()

@Transactional(readOnly = true)
public boolean isMenuAvailable(Long menuId)
```

---

### TableSessionService

#### Purpose
테이블 세션 관리 로직 처리

#### Responsibilities
- 세션 시작 로직 (첫 주문 시)
- 세션 종료 로직 (매장 이용 완료)
- 세션 검증 로직
- 현재 활성 세션 조회

#### Dependencies
- AdminMapper (데이터 액세스)

#### Transaction Boundaries
- `startSession()`: 트랜잭션 필요 (세션 생성)
- `endSession()`: 트랜잭션 필요 (세션 종료 + 주문 이력 이동)

#### Key Methods
```java
@Transactional
public TableSession startSession(Long tableId)

@Transactional
public void endSession(Long tableId)

@Transactional(readOnly = true)
public boolean isSessionActive(String sessionId)

@Transactional(readOnly = true)
public TableSession getActiveSession(Long tableId)
```

---

## Service Orchestration Patterns

### Pattern 1: Order Creation Flow
```
CustomerController.createOrder()
  → CustomerService.createOrder()
    → TableSessionService.getActiveSession() or startSession()
    → OrderService.createOrder()
      → CustomerMapper.insertOrder()
      → CustomerMapper.insertOrderItems()
      → OrderService.publishNewOrderEvent()
```

### Pattern 2: Order Status Update Flow
```
AdminController.updateOrderStatus()
  → AdminService.updateOrderStatus()
    → OrderService.updateOrderStatus()
      → AdminMapper.updateOrderStatus()
      → OrderService.publishOrderStatusChangeEvent()
```

### Pattern 3: Table Session End Flow
```
AdminController.endTableSession()
  → AdminService.endTableSession()
    → TableSessionService.endSession()
      → AdminMapper.updateTableSessionEnd()
      → AdminMapper.moveOrdersToHistory()
```

### Pattern 4: Order Cancellation Flow
```
CustomerController.cancelOrder()
  → CustomerService.cancelOrder()
    → OrderService.canCancelOrder()
    → CustomerMapper.deleteOrder()
```

---

## Service Interaction Matrix

| Service | CustomerService | AdminService | OrderService | MenuService | TableSessionService |
|---------|----------------|--------------|--------------|-------------|---------------------|
| **CustomerService** | - | No | Yes | Yes | Yes |
| **AdminService** | No | - | Yes | No | Yes |
| **OrderService** | No | No | - | No | No |
| **MenuService** | No | No | No | - | No |
| **TableSessionService** | No | No | No | No | - |

**Legend**:
- **Yes**: Direct dependency
- **No**: No direct dependency

---

## SSE (Server-Sent Events) Management

### SseEmitterManager

#### Purpose
SSE 연결 관리 및 이벤트 브로드캐스팅

#### Responsibilities
- SSE 연결 생성 및 관리
- 이벤트 브로드캐스팅 (모든 연결된 클라이언트에게)
- 연결 타임아웃 처리
- 연결 종료 처리

#### Key Methods
```java
public SseEmitter createEmitter()
public void sendEvent(String eventName, Object data)
public void removeEmitter(SseEmitter emitter)
public void handleTimeout(SseEmitter emitter)
public void handleError(SseEmitter emitter, Throwable throwable)
```

#### Event Types
- `new-order`: 신규 주문 생성
- `order-status-change`: 주문 상태 변경
- `order-deleted`: 주문 삭제

---

## Transaction Management Strategy

### Read-Only Transactions
- 모든 조회 메서드: `@Transactional(readOnly = true)`
- 성능 최적화 (읽기 전용 힌트)

### Write Transactions
- 생성/수정/삭제 메서드: `@Transactional`
- 롤백 정책: RuntimeException 발생 시 자동 롤백

### Transaction Propagation
- 기본값: `REQUIRED` (기존 트랜잭션 참여 또는 새로 생성)
- SSE 이벤트 발행: 트랜잭션 커밋 후 실행 (`@TransactionalEventListener`)

---

## Error Handling in Services

### Exception Types
- `AuthenticationException`: 인증 실패
- `ValidationException`: 입력 검증 실패
- `ResourceNotFoundException`: 리소스 없음
- `BusinessException`: 비즈니스 로직 위반

### Exception Propagation
- Service 레이어에서 예외 발생
- GlobalExceptionHandler에서 캐치 및 처리
- 표준화된 에러 응답 반환

---

## Service Layer Best Practices

1. **Thin Services**: 비즈니스 로직 최소화, CRUD 중심
2. **Single Responsibility**: 각 서비스는 하나의 도메인 책임
3. **Transaction Boundaries**: 명확한 트랜잭션 경계 설정
4. **DTO Conversion**: Service 레이어에서 Entity → DTO 변환
5. **Validation**: Controller에서 기본 검증, Service에서 비즈니스 검증
6. **Error Handling**: 명확한 예외 타입 사용
7. **Logging**: 중요한 비즈니스 이벤트 로깅

---

## Notes

- Detailed business logic will be defined in Functional Design (per-unit, CONSTRUCTION phase)
- Service implementations will be generated in Code Generation phase
