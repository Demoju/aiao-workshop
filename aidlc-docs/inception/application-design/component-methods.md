# Component Methods - 테이블오더 서비스

## Frontend Component Methods

### Customer Domain

#### MenuPage
```typescript
interface MenuPageProps {}

// Hooks
useMenu(): { menus: Menu[], categories: Category[], loading: boolean, error: Error | null }
useCart(): { addToCart: (menu: Menu, quantity: number) => void }

// Methods
handleAddToCart(menu: Menu, quantity: number): void
handleCategoryChange(categoryId: string): void
```

#### CartPage
```typescript
interface CartPageProps {}

// Hooks
useCart(): { 
  items: CartItem[], 
  totalAmount: number, 
  updateQuantity: (itemId: string, quantity: number) => void,
  removeItem: (itemId: string) => void,
  clearCart: () => void
}
useOrder(): { createOrder: (items: CartItem[]) => Promise<Order> }

// Methods
handleQuantityChange(itemId: string, quantity: number): void
handleRemoveItem(itemId: string): void
handleClearCart(): void
handleCheckout(): Promise<void>
```

#### OrderHistoryPage
```typescript
interface OrderHistoryPageProps {}

// Hooks
useOrderHistory(): { orders: Order[], loading: boolean, error: Error | null }
useOrder(): { cancelOrder: (orderId: string) => Promise<void> }

// Methods
handleCancelOrder(orderId: string): Promise<void>
handleRefresh(): void
```

#### TableLoginPage
```typescript
interface TableLoginPageProps {}

// Hooks
useAuth(): { login: (storeId: string, tableNumber: string, password: string) => Promise<void> }

// Methods
handleLogin(storeId: string, tableNumber: string, password: string): Promise<void>
handleInputChange(field: string, value: string): void
```

#### MenuCard
```typescript
interface MenuCardProps {
  menu: Menu
  onAddToCart: (menu: Menu, quantity: number) => void
}

// Methods
handleAddClick(): void
```

#### CartItem
```typescript
interface CartItemProps {
  item: CartItem
  onQuantityChange: (itemId: string, quantity: number) => void
  onRemove: (itemId: string) => void
}

// Methods
handleIncrement(): void
handleDecrement(): void
handleRemove(): void
```

#### OrderCard
```typescript
interface OrderCardProps {
  order: Order
  onCancel?: (orderId: string) => void
}

// Methods
handleCancel(): void
handleExpand(): void
```

---

### Admin Domain

#### AdminLoginPage
```typescript
interface AdminLoginPageProps {}

// Hooks
useAuth(): { login: (storeId: string, username: string, password: string) => Promise<void> }

// Methods
handleLogin(storeId: string, username: string, password: string): Promise<void>
handleInputChange(field: string, value: string): void
```

#### AdminDashboardPage
```typescript
interface AdminDashboardPageProps {}

// Hooks
useOrderStream(): { orders: Order[], tables: Table[], loading: boolean }
useOrder(): { 
  updateOrderStatus: (orderId: string, status: OrderStatus) => Promise<void>,
  deleteOrder: (orderId: string) => Promise<void>
}

// Methods
handleOrderStatusChange(orderId: string, status: OrderStatus): Promise<void>
handleOrderDelete(orderId: string): Promise<void>
handleTableFilter(tableId: string): void
handleOrderClick(orderId: string): void
```

#### TableManagementPage
```typescript
interface TableManagementPageProps {}

// Hooks
useTable(): { 
  tables: Table[], 
  endSession: (tableId: string) => Promise<void>,
  getPastOrders: (tableId: string, startDate: Date, endDate: Date) => Promise<Order[]>
}

// Methods
handleEndSession(tableId: string): Promise<void>
handleViewPastOrders(tableId: string): void
```

#### MenuManagementPage
```typescript
interface MenuManagementPageProps {}

// Hooks
useMenu(): { menus: Menu[], categories: Category[], loading: boolean }

// Methods
handleCategoryFilter(categoryId: string): void
handleMenuClick(menuId: string): void
```

#### TableCard
```typescript
interface TableCardProps {
  table: Table
  orders: Order[]
  totalAmount: number
  onClick: (tableId: string) => void
}

// Methods
handleClick(): void
```

#### OrderDetailModal
```typescript
interface OrderDetailModalProps {
  order: Order
  onClose: () => void
  onStatusChange: (orderId: string, status: OrderStatus) => void
  onDelete: (orderId: string) => void
}

// Methods
handleStatusChange(status: OrderStatus): void
handleDelete(): void
handleClose(): void
```

#### PastOrdersModal
```typescript
interface PastOrdersModalProps {
  tableId: string
  orders: Order[]
  onClose: () => void
}

// Methods
handleDateFilter(startDate: Date, endDate: Date): void
handleClose(): void
```

---

### Shared Domain

#### Header
```typescript
interface HeaderProps {
  isAdmin: boolean
  onLogout?: () => void
  onLanguageChange: (lang: 'ko' | 'en') => void
}

// Methods
handleLogout(): void
handleLanguageChange(lang: 'ko' | 'en'): void
```

#### ErrorBoundary
```typescript
interface ErrorBoundaryProps {
  children: React.ReactNode
}

interface ErrorBoundaryState {
  hasError: boolean
  error: Error | null
}

// Methods
static getDerivedStateFromError(error: Error): ErrorBoundaryState
componentDidCatch(error: Error, errorInfo: React.ErrorInfo): void
handleReset(): void
```

#### LoadingSpinner
```typescript
interface LoadingSpinnerProps {
  size?: 'small' | 'medium' | 'large'
  fullScreen?: boolean
}
```

#### ConfirmDialog
```typescript
interface ConfirmDialogProps {
  open: boolean
  title: string
  message: string
  onConfirm: () => void
  onCancel: () => void
}

// Methods
handleConfirm(): void
handleCancel(): void
```

---

## Custom Hooks

### useMenu
```typescript
interface UseMenuReturn {
  menus: Menu[]
  categories: Category[]
  loading: boolean
  error: Error | null
  fetchMenus: () => Promise<void>
  fetchMenusByCategory: (categoryId: string) => Promise<void>
}

function useMenu(): UseMenuReturn
```

### useCart
```typescript
interface UseCartReturn {
  items: CartItem[]
  totalAmount: number
  addToCart: (menu: Menu, quantity: number) => void
  updateQuantity: (itemId: string, quantity: number) => void
  removeItem: (itemId: string) => void
  clearCart: () => void
}

function useCart(): UseCartReturn
```

### useOrder
```typescript
interface UseOrderReturn {
  createOrder: (items: CartItem[]) => Promise<Order>
  cancelOrder: (orderId: string) => Promise<void>
  updateOrderStatus: (orderId: string, status: OrderStatus) => Promise<void>
  deleteOrder: (orderId: string) => Promise<void>
}

function useOrder(): UseOrderReturn
```

### useOrderHistory
```typescript
interface UseOrderHistoryReturn {
  orders: Order[]
  loading: boolean
  error: Error | null
  fetchOrders: () => Promise<void>
}

function useOrderHistory(): UseOrderHistoryReturn
```

### useOrderStream (SSE)
```typescript
interface UseOrderStreamReturn {
  orders: Order[]
  tables: Table[]
  loading: boolean
  error: Error | null
}

function useOrderStream(): UseOrderStreamReturn
```

### useAuth
```typescript
interface UseAuthReturn {
  isAuthenticated: boolean
  user: User | null
  login: (storeId: string, username: string, password: string) => Promise<void>
  logout: () => void
}

function useAuth(): UseAuthReturn
```

### useTable
```typescript
interface UseTableReturn {
  tables: Table[]
  loading: boolean
  error: Error | null
  endSession: (tableId: string) => Promise<void>
  getPastOrders: (tableId: string, startDate: Date, endDate: Date) => Promise<Order[]>
}

function useTable(): UseTableReturn
```

---

## Backend Component Methods

### Controller Layer

#### CustomerController
```java
@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    
    // 메뉴 조회
    @GetMapping("/menus")
    public ResponseEntity<List<MenuResponseDto>> getMenus(
        @RequestParam(required = false) Long categoryId
    )
    
    // 주문 생성
    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDto> createOrder(
        @RequestBody OrderRequestDto orderRequest
    )
    
    // 주문 내역 조회 (현재 세션)
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrders(
        @RequestParam Long tableId,
        @RequestParam String sessionId
    )
    
    // 주문 취소
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(
        @PathVariable Long orderId
    )
    
    // 테이블 로그인
    @PostMapping("/login")
    public ResponseEntity<TableLoginResponseDto> login(
        @RequestBody TableLoginRequestDto loginRequest
    )
    
    // 카테고리 조회
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDto>> getCategories()
}
```

#### AdminController
```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    // 관리자 로그인
    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponseDto> login(
        @RequestBody AdminLoginRequestDto loginRequest
    )
    
    // 주문 목록 조회
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrders(
        @RequestParam(required = false) Long tableId
    )
    
    // 주문 상태 변경
    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
        @PathVariable Long orderId,
        @RequestBody OrderStatusUpdateDto statusUpdate
    )
    
    // 주문 삭제
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> deleteOrder(
        @PathVariable Long orderId
    )
    
    // 테이블 세션 종료
    @PostMapping("/tables/{tableId}/end-session")
    public ResponseEntity<Void> endTableSession(
        @PathVariable Long tableId
    )
    
    // 과거 주문 내역 조회
    @GetMapping("/tables/{tableId}/past-orders")
    public ResponseEntity<List<OrderResponseDto>> getPastOrders(
        @PathVariable Long tableId,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate
    )
    
    // SSE 엔드포인트 (실시간 주문 업데이트)
    @GetMapping(value = "/orders/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOrders()
}
```

#### MenuController
```java
@RestController
@RequestMapping("/api/menus")
public class MenuController {
    
    // 메뉴 목록 조회
    @GetMapping
    public ResponseEntity<List<MenuResponseDto>> getMenus(
        @RequestParam(required = false) Long categoryId
    )
    
    // 메뉴 상세 조회
    @GetMapping("/{menuId}")
    public ResponseEntity<MenuResponseDto> getMenu(
        @PathVariable Long menuId
    )
    
    // 카테고리 목록 조회
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDto>> getCategories()
}
```

---

### Service Layer

#### CustomerService
```java
@Service
public class CustomerService {
    
    // 메뉴 조회
    public List<MenuResponseDto> getMenus(Long categoryId)
    
    // 주문 생성 (세션 시작 포함)
    public OrderResponseDto createOrder(OrderRequestDto orderRequest)
    
    // 주문 내역 조회 (현재 세션만)
    public List<OrderResponseDto> getOrders(Long tableId, String sessionId)
    
    // 주문 취소 (수락 전 검증)
    public void cancelOrder(Long orderId)
    
    // 테이블 로그인 검증
    public TableLoginResponseDto login(TableLoginRequestDto loginRequest)
    
    // 카테고리 조회
    public List<CategoryResponseDto> getCategories()
}
```

#### AdminService
```java
@Service
public class AdminService {
    
    // 관리자 인증 (JWT 발급)
    public AdminLoginResponseDto login(AdminLoginRequestDto loginRequest)
    
    // 주문 목록 조회
    public List<OrderResponseDto> getOrders(Long tableId)
    
    // 주문 상태 변경
    public void updateOrderStatus(Long orderId, OrderStatus status)
    
    // 주문 삭제
    public void deleteOrder(Long orderId)
    
    // 테이블 세션 종료
    public void endTableSession(Long tableId)
    
    // 과거 주문 내역 조회
    public List<OrderResponseDto> getPastOrders(Long tableId, LocalDate startDate, LocalDate endDate)
}
```

#### OrderService
```java
@Service
public class OrderService {
    
    // 주문 생성 처리
    public Order createOrder(OrderRequestDto orderRequest)
    
    // 주문 상태 관리
    public void updateOrderStatus(Long orderId, OrderStatus status)
    
    // SSE 이벤트 발행 (신규 주문)
    public void publishNewOrderEvent(Order order)
    
    // SSE 이벤트 발행 (상태 변경)
    public void publishOrderStatusChangeEvent(Order order)
    
    // 주문 검증
    public boolean validateOrder(OrderRequestDto orderRequest)
    
    // 주문 취소 가능 여부 확인
    public boolean canCancelOrder(Long orderId)
}
```

#### MenuService
```java
@Service
public class MenuService {
    
    // 메뉴 조회
    public List<Menu> getMenus(Long categoryId)
    
    // 메뉴 상세 조회
    public Menu getMenu(Long menuId)
    
    // 카테고리 조회
    public List<Category> getCategories()
    
    // 메뉴 가용성 검증
    public boolean isMenuAvailable(Long menuId)
}
```

#### TableSessionService
```java
@Service
public class TableSessionService {
    
    // 세션 시작 (첫 주문 시)
    public TableSession startSession(Long tableId)
    
    // 세션 종료 (매장 이용 완료)
    public void endSession(Long tableId)
    
    // 세션 검증
    public boolean isSessionActive(String sessionId)
    
    // 현재 활성 세션 조회
    public TableSession getActiveSession(Long tableId)
}
```

---

### Repository Layer (MyBatis Mapper)

#### CustomerMapper
```java
@Mapper
public interface CustomerMapper {
    
    // 메뉴 조회
    List<Menu> selectMenus(@Param("categoryId") Long categoryId);
    
    // 주문 생성
    void insertOrder(Order order);
    
    // 주문 항목 생성
    void insertOrderItems(@Param("orderItems") List<OrderItem> orderItems);
    
    // 주문 내역 조회 (현재 세션)
    List<Order> selectOrdersBySession(@Param("tableId") Long tableId, @Param("sessionId") String sessionId);
    
    // 주문 취소
    void deleteOrder(@Param("orderId") Long orderId);
    
    // 테이블 로그인 검증
    Table selectTableByCredentials(@Param("storeId") Long storeId, @Param("tableNumber") String tableNumber, @Param("password") String password);
    
    // 카테고리 조회
    List<Category> selectCategories();
}
```

#### AdminMapper
```java
@Mapper
public interface AdminMapper {
    
    // 관리자 인증
    Admin selectAdminByCredentials(@Param("storeId") Long storeId, @Param("username") String username);
    
    // 주문 목록 조회
    List<Order> selectOrders(@Param("tableId") Long tableId);
    
    // 주문 상태 변경
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);
    
    // 주문 삭제
    void deleteOrder(@Param("orderId") Long orderId);
    
    // 테이블 세션 종료
    void updateTableSessionEnd(@Param("tableId") Long tableId);
    
    // 과거 주문 내역 조회
    List<Order> selectPastOrders(@Param("tableId") Long tableId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // 테이블 목록 조회
    List<Table> selectTables();
}
```

---

### Security & Middleware

#### JwtAuthenticationFilter
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain
    ) throws ServletException, IOException
    
    // JWT 추출
    private String extractJwtFromRequest(HttpServletRequest request)
    
    // JWT 검증
    private boolean validateToken(String token)
    
    // 인증 정보 설정
    private void setAuthentication(String token)
}
```

#### GlobalExceptionHandler
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    // 일반 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex)
    
    // 인증 예외 처리
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(AuthenticationException ex)
    
    // 검증 예외 처리
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(ValidationException ex)
    
    // 리소스 없음 예외 처리
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex)
}
```

---

## Notes

- **Detailed business logic** will be defined in Functional Design (per-unit, CONSTRUCTION phase)
- **Method implementations** will be generated in Code Generation phase
- **Input/output types** are high-level; detailed DTOs will be defined in NFR Design phase
