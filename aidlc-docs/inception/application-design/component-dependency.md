# Component Dependencies - 테이블오더 서비스

## Dependency Overview

### Frontend Dependencies
- **Customer Domain** → Shared Domain
- **Admin Domain** → Shared Domain
- **Custom Hooks** → API Services → Backend REST API

### Backend Dependencies
- **Controller Layer** → Service Layer
- **Service Layer** → Repository Layer (MyBatis Mapper)
- **Service Layer** → Service Layer (orchestration)
- **All Layers** → Domain Layer (entities)

---

## Frontend Component Dependency Matrix

| Component | Dependencies |
|-----------|-------------|
| **MenuPage** | useMenu, useCart, MenuCard, LoadingSpinner, ErrorBoundary |
| **CartPage** | useCart, useOrder, CartItem, LoadingSpinner, ConfirmDialog |
| **OrderHistoryPage** | useOrderHistory, useOrder, OrderCard, LoadingSpinner |
| **TableLoginPage** | useAuth, LoadingSpinner |
| **MenuCard** | - |
| **CartItem** | - |
| **OrderCard** | - |
| **AdminLoginPage** | useAuth, LoadingSpinner |
| **AdminDashboardPage** | useOrderStream, useOrder, TableCard, OrderDetailModal, LoadingSpinner |
| **TableManagementPage** | useTable, PastOrdersModal, LoadingSpinner, ConfirmDialog |
| **MenuManagementPage** | useMenu, LoadingSpinner |
| **TableCard** | - |
| **OrderDetailModal** | ConfirmDialog |
| **PastOrdersModal** | - |
| **Header** | useAuth |
| **ErrorBoundary** | - |
| **LoadingSpinner** | - |
| **ConfirmDialog** | - |

---

## Frontend Hook Dependencies

| Hook | Dependencies | Backend API |
|------|-------------|-------------|
| **useMenu** | Axios interceptor | GET /api/customer/menus, GET /api/customer/categories |
| **useCart** | LocalStorage, Zustand | - |
| **useOrder** | Axios interceptor, Zustand | POST /api/customer/orders, DELETE /api/customer/orders/{orderId}, PATCH /api/admin/orders/{orderId}/status, DELETE /api/admin/orders/{orderId} |
| **useOrderHistory** | Axios interceptor | GET /api/customer/orders |
| **useOrderStream** | EventSource (SSE) | GET /api/admin/orders/stream |
| **useAuth** | Axios interceptor, SessionStorage, Zustand | POST /api/customer/login, POST /api/admin/login |
| **useTable** | Axios interceptor | POST /api/admin/tables/{tableId}/end-session, GET /api/admin/tables/{tableId}/past-orders |

---

## Backend Component Dependency Matrix

| Component | Dependencies |
|-----------|-------------|
| **CustomerController** | CustomerService |
| **AdminController** | AdminService, SseEmitterManager |
| **MenuController** | MenuService |
| **CustomerService** | CustomerMapper, TableSessionService, OrderService |
| **AdminService** | AdminMapper, JwtTokenProvider, PasswordEncoder, OrderService, TableSessionService |
| **OrderService** | CustomerMapper, AdminMapper, SseEmitterManager |
| **MenuService** | CustomerMapper |
| **TableSessionService** | AdminMapper |
| **CustomerMapper** | MyBatis SqlSessionFactory |
| **AdminMapper** | MyBatis SqlSessionFactory |
| **JwtAuthenticationFilter** | JwtTokenProvider |
| **GlobalExceptionHandler** | - |

---

## Data Flow Diagrams

### Customer Order Creation Flow

```
[Customer UI: CartPage]
        |
        | handleCheckout()
        v
[Custom Hook: useOrder.createOrder()]
        |
        | POST /api/customer/orders
        v
[Backend: CustomerController.createOrder()]
        |
        | CustomerService.createOrder()
        v
[CustomerService]
        |
        +---> TableSessionService.getActiveSession() or startSession()
        |
        +---> OrderService.createOrder()
        |           |
        |           +---> CustomerMapper.insertOrder()
        |           |
        |           +---> CustomerMapper.insertOrderItems()
        |           |
        |           +---> OrderService.publishNewOrderEvent()
        |                       |
        |                       v
        |               [SseEmitterManager]
        |                       |
        |                       v
        |               [Admin UI: AdminDashboardPage]
        |                       |
        |                       v
        |               [useOrderStream receives event]
        v
[Response: OrderResponseDto]
        |
        v
[Customer UI: Order confirmation + redirect to MenuPage]
```

---

### Admin Order Status Update Flow

```
[Admin UI: OrderDetailModal]
        |
        | handleStatusChange()
        v
[Custom Hook: useOrder.updateOrderStatus()]
        |
        | PATCH /api/admin/orders/{orderId}/status
        v
[Backend: AdminController.updateOrderStatus()]
        |
        | AdminService.updateOrderStatus()
        v
[AdminService]
        |
        +---> OrderService.updateOrderStatus()
                    |
                    +---> AdminMapper.updateOrderStatus()
                    |
                    +---> OrderService.publishOrderStatusChangeEvent()
                                |
                                v
                        [SseEmitterManager]
                                |
                                v
                        [Admin UI: AdminDashboardPage]
                                |
                                v
                        [useOrderStream receives event]
                                |
                                v
                        [UI updates order status]
```

---

### Table Session End Flow

```
[Admin UI: TableManagementPage]
        |
        | handleEndSession()
        v
[Custom Hook: useTable.endSession()]
        |
        | POST /api/admin/tables/{tableId}/end-session
        v
[Backend: AdminController.endTableSession()]
        |
        | AdminService.endTableSession()
        v
[AdminService]
        |
        +---> TableSessionService.endSession()
                    |
                    +---> AdminMapper.updateTableSessionEnd()
                    |
                    +---> AdminMapper.moveOrdersToHistory()
                                |
                                v
                        [Database: Orders moved to history]
                                |
                                v
                        [Response: Success]
                                |
                                v
                        [Admin UI: Table reset confirmation]
```

---

### SSE Real-time Order Update Flow

```
[Admin UI: AdminDashboardPage mounts]
        |
        | useOrderStream() initializes
        v
[EventSource connection established]
        |
        | GET /api/admin/orders/stream
        v
[Backend: AdminController.streamOrders()]
        |
        | SseEmitterManager.createEmitter()
        v
[SseEmitter created and stored]
        |
        | (waiting for events)
        v
[New order created by customer]
        |
        | OrderService.publishNewOrderEvent()
        v
[SseEmitterManager.sendEvent("new-order", orderData)]
        |
        | Broadcast to all connected emitters
        v
[EventSource receives event]
        |
        | useOrderStream updates state
        v
[Admin UI: New order appears with visual emphasis]
```

---

## API Endpoint Mapping

### Customer API Endpoints

| Endpoint | Method | Controller | Service | Mapper |
|----------|--------|------------|---------|--------|
| `/api/customer/menus` | GET | CustomerController | CustomerService | CustomerMapper |
| `/api/customer/categories` | GET | CustomerController | CustomerService | CustomerMapper |
| `/api/customer/orders` | POST | CustomerController | CustomerService | CustomerMapper, OrderService |
| `/api/customer/orders` | GET | CustomerController | CustomerService | CustomerMapper |
| `/api/customer/orders/{orderId}` | DELETE | CustomerController | CustomerService | CustomerMapper, OrderService |
| `/api/customer/login` | POST | CustomerController | CustomerService | CustomerMapper |

### Admin API Endpoints

| Endpoint | Method | Controller | Service | Mapper |
|----------|--------|------------|---------|--------|
| `/api/admin/login` | POST | AdminController | AdminService | AdminMapper |
| `/api/admin/orders` | GET | AdminController | AdminService | AdminMapper |
| `/api/admin/orders/{orderId}/status` | PATCH | AdminController | AdminService | AdminMapper, OrderService |
| `/api/admin/orders/{orderId}` | DELETE | AdminController | AdminService | AdminMapper |
| `/api/admin/tables/{tableId}/end-session` | POST | AdminController | AdminService | AdminMapper, TableSessionService |
| `/api/admin/tables/{tableId}/past-orders` | GET | AdminController | AdminService | AdminMapper |
| `/api/admin/orders/stream` | GET (SSE) | AdminController | SseEmitterManager | - |

### Menu API Endpoints

| Endpoint | Method | Controller | Service | Mapper |
|----------|--------|------------|---------|--------|
| `/api/menus` | GET | MenuController | MenuService | CustomerMapper |
| `/api/menus/{menuId}` | GET | MenuController | MenuService | CustomerMapper |
| `/api/menus/categories` | GET | MenuController | MenuService | CustomerMapper |

---

## Communication Patterns

### Frontend → Backend

#### Pattern 1: REST API Call (Custom Hooks)
```typescript
// Custom hook encapsulates API call
const useOrder = () => {
  const createOrder = async (items: CartItem[]) => {
    const response = await axios.post('/api/customer/orders', {
      items,
      tableId,
      sessionId
    });
    return response.data;
  };
  
  return { createOrder };
};

// Component uses hook
const CartPage = () => {
  const { createOrder } = useOrder();
  
  const handleCheckout = async () => {
    await createOrder(cartItems);
  };
};
```

#### Pattern 2: SSE Connection (EventSource)
```typescript
// Custom hook manages SSE connection
const useOrderStream = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  
  useEffect(() => {
    const eventSource = new EventSource('/api/admin/orders/stream');
    
    eventSource.addEventListener('new-order', (event) => {
      const newOrder = JSON.parse(event.data);
      setOrders(prev => [...prev, newOrder]);
    });
    
    return () => eventSource.close();
  }, []);
  
  return { orders };
};
```

#### Pattern 3: Axios Interceptor (JWT Token)
```typescript
// Axios interceptor adds JWT token to all requests
axios.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

---

### Backend Layer Communication

#### Pattern 1: Controller → Service
```java
@RestController
public class CustomerController {
    private final CustomerService customerService;
    
    @PostMapping("/api/customer/orders")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto request) {
        OrderResponseDto response = customerService.createOrder(request);
        return ResponseEntity.ok(response);
    }
}
```

#### Pattern 2: Service → Service (Orchestration)
```java
@Service
public class CustomerService {
    private final TableSessionService tableSessionService;
    private final OrderService orderService;
    
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {
        // Get or create session
        TableSession session = tableSessionService.getActiveSession(request.getTableId());
        if (session == null) {
            session = tableSessionService.startSession(request.getTableId());
        }
        
        // Create order
        Order order = orderService.createOrder(request);
        
        return toDto(order);
    }
}
```

#### Pattern 3: Service → Mapper (Data Access)
```java
@Service
public class MenuService {
    private final CustomerMapper customerMapper;
    
    @Transactional(readOnly = true)
    public List<Menu> getMenus(Long categoryId) {
        return customerMapper.selectMenus(categoryId);
    }
}
```

---

## State Management (Frontend)

### Zustand Store Structure

```typescript
// Global state store
interface AppState {
  // Auth state
  isAuthenticated: boolean;
  user: User | null;
  token: string | null;
  
  // Cart state
  cartItems: CartItem[];
  
  // Order stream state (Admin)
  orders: Order[];
  tables: Table[];
  
  // Actions
  login: (token: string, user: User) => void;
  logout: () => void;
  addToCart: (item: CartItem) => void;
  updateCartItem: (itemId: string, quantity: number) => void;
  removeFromCart: (itemId: string) => void;
  clearCart: () => void;
  updateOrders: (orders: Order[]) => void;
}
```

---

## Security Flow

### JWT Authentication Flow

```
[Admin Login]
        |
        | POST /api/admin/login
        v
[AdminController.login()]
        |
        | AdminService.login()
        v
[AdminService]
        |
        +---> AdminMapper.selectAdminByCredentials()
        |
        +---> PasswordEncoder.matches()
        |
        +---> JwtTokenProvider.generateToken()
        v
[Response: JWT token]
        |
        v
[Frontend: Store token in SessionStorage]
        |
        v
[Subsequent API calls include token in Authorization header]
        |
        | Authorization: Bearer <token>
        v
[JwtAuthenticationFilter.doFilterInternal()]
        |
        +---> extractJwtFromRequest()
        |
        +---> validateToken()
        |
        +---> setAuthentication()
        v
[Request proceeds to Controller]
```

---

## Error Handling Flow

```
[Exception thrown in Service]
        |
        v
[GlobalExceptionHandler catches exception]
        |
        +---> Determine exception type
        |
        +---> Create ErrorResponseDto
        |
        +---> Set HTTP status code
        |
        +---> Log error
        v
[Response: ErrorResponseDto]
        |
        v
[Frontend: Axios interceptor catches error]
        |
        v
[ErrorBoundary or local error handler displays message]
```

---

## Database Access Pattern

### MyBatis Mapper Pattern

```
[Service calls Mapper method]
        |
        v
[MyBatis Mapper Interface]
        |
        v
[MyBatis XML file with SQL query]
        |
        v
[Database query execution]
        |
        v
[Result mapping to Entity]
        |
        v
[Return to Service]
```

---

## Notes

- All dependencies are unidirectional (no circular dependencies)
- Frontend uses custom hooks for API abstraction
- Backend uses layered architecture with clear separation
- SSE provides real-time updates without polling
- JWT tokens managed via Axios interceptors
- Zustand provides centralized state management
