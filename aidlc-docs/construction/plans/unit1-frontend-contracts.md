# Contract/Interface Definition - Unit 1 (Frontend)

## Unit Context
- **Stories**: Customer 메뉴 조회, 장바구니, 주문, Admin 대시보드, 테이블 관리
- **Dependencies**: Backend Customer API (Unit 2), Backend Admin API (Unit 3) - Mock 사용
- **Tech Stack**: React 18, TypeScript, Zustand, Axios, React Router, Tailwind CSS, Vite

---

## Types Layer
### `frontend/src/types/index.ts`
- Domain interfaces: Menu, Category, CartItem, Order, OrderItem, OrderStatus, TableSession, TableWithOrders
- DTO interfaces: OrderRequestDto, OrderItemRequestDto, TableLoginRequestDto, TableLoginResponseDto, AdminLoginRequestDto, AdminLoginResponseDto, OrderStatusUpdateDto, ErrorResponseDto
- UI State interfaces: ApiError
- SSE interfaces: SseEvent, NewOrderEvent, OrderStatusChangeEvent, OrderDeletedEvent

### `frontend/src/types/schemas.ts`
- `tableLoginSchema`: Zod schema (storeId: number, tableNumber: string.min(1), password: string.min(1))
- `adminLoginSchema`: Zod schema (storeId: number, username: string.min(1), password: string.min(1))

---

## Store Layer (Zustand)

### `frontend/src/store/cartStore.ts` - CartStore
- `items: CartItem[]`
- `totalAmount: number`
- `addToCart(menu: Menu, quantity: number): void` - 중복 시 수량 증가, max 99
- `updateQuantity(menuId: number, quantity: number): void` - 0이면 삭제
- `removeItem(menuId: number): void`
- `clearCart(): void`
- persist: localStorage

### `frontend/src/store/authStore.ts` - AuthStore
- `isAuthenticated: boolean`
- `user: { role: 'customer' | 'admin', tableId?: number, sessionId?: string, token?: string, storeId?: number } | null`
- `setAuth(user): void`
- `logout(): void`
- persist: sessionStorage

### `frontend/src/store/orderStore.ts` - OrderStore
- `tables: TableWithOrders[]`
- `customerOrders: Order[]`
- `setTables(tables: TableWithOrders[]): void`
- `setCustomerOrders(orders: Order[]): void`
- `addOrder(order: Order): void`
- `updateOrderStatus(orderId: number, status: OrderStatus): void`
- `removeOrder(orderId: number): void`

### `frontend/src/store/loadingStore.ts` - LoadingStore
- `isLoading: boolean`
- `setLoading(value: boolean): void`

---

## Service Layer (API)

### `frontend/src/services/api.ts`
- `apiClient`: Axios instance (baseURL, interceptors)
  - Request interceptor: JWT 토큰 첨부
  - Response interceptor: 에러 처리, 401 → logout

### `frontend/src/services/customerApi.ts`
- `getMenus(storeId: number): Promise<Menu[]>`
- `getCategories(storeId: number): Promise<Category[]>`
- `createOrder(dto: OrderRequestDto): Promise<Order>`
- `getOrders(tableId: number, sessionId: string): Promise<Order[]>`
- `cancelOrder(orderId: number): Promise<void>`
- `tableLogin(dto: TableLoginRequestDto): Promise<TableLoginResponseDto>`

### `frontend/src/services/adminApi.ts`
- `adminLogin(dto: AdminLoginRequestDto): Promise<AdminLoginResponseDto>`
- `getOrders(): Promise<TableWithOrders[]>`
- `updateOrderStatus(orderId: number, dto: OrderStatusUpdateDto): Promise<void>`
- `deleteOrder(orderId: number): Promise<void>`
- `endTableSession(tableId: number): Promise<void>`
- `getPastOrders(tableId: number): Promise<Order[]>`

---

## Hook Layer

### `frontend/src/hooks/useMenu.ts`
- `useMenu(storeId: number): { menus, categories, isLoading, error, refetch }`

### `frontend/src/hooks/useCart.ts`
- `useCart(): { items, totalAmount, addToCart, updateQuantity, removeItem, clearCart }`

### `frontend/src/hooks/useOrder.ts`
- `useOrder(): { orders, createOrder, cancelOrder, isLoading, error }`

### `frontend/src/hooks/useOrderHistory.ts`
- `useOrderHistory(tableId, sessionId): { orders, isLoading, error, refetch }`

### `frontend/src/hooks/useOrderStream.ts`
- `useOrderStream(token: string): { isConnected, error, reconnect }`
  - SSE 연결, 즉시 재시도 max 3회

### `frontend/src/hooks/useAuth.ts`
- `useAuth(): { isAuthenticated, user, login, logout }`

### `frontend/src/hooks/useTable.ts`
- `useTable(): { endSession, deletePastOrders, getPastOrders, isLoading }`

---

## Component Layer

### Shared Components
- `frontend/src/shared/ErrorBoundary.tsx` - Global error boundary
- `frontend/src/shared/LoadingSpinner.tsx` - Global loading indicator
- `frontend/src/shared/ConfirmDialog.tsx` - Confirmation modal
- `frontend/src/shared/LazyImage.tsx` - Intersection Observer lazy loading

### Customer Components
- `frontend/src/customer/components/MenuCard.tsx` - 메뉴 카드 (props: menu, onAddToCart)
- `frontend/src/customer/components/CartItem.tsx` - 장바구니 항목 (props: item, onUpdateQuantity, onRemove)
- `frontend/src/customer/components/OrderCard.tsx` - 주문 카드 (props: order, onCancel?)

### Admin Components
- `frontend/src/admin/components/TableCard.tsx` - 테이블 카드 (props: tableWithOrders, onEndSession, onOrderClick)
- `frontend/src/admin/components/OrderDetailModal.tsx` - 주문 상세 모달 (props: order, onStatusChange, onDelete, onClose)
- `frontend/src/admin/components/PastOrdersModal.tsx` - 과거 주문 모달 (props: tableId, onClose)

### Pages
- `frontend/src/customer/pages/MenuPage.tsx`
- `frontend/src/customer/pages/CartPage.tsx`
- `frontend/src/customer/pages/OrderHistoryPage.tsx`
- `frontend/src/customer/pages/TableLoginPage.tsx`
- `frontend/src/admin/pages/AdminLoginPage.tsx`
- `frontend/src/admin/pages/AdminDashboardPage.tsx`
- `frontend/src/admin/pages/TableManagementPage.tsx`
- `frontend/src/admin/pages/MenuManagementPage.tsx`

---

## App Entry

### `frontend/src/App.tsx`
- React Router with lazy loading
- ErrorBoundary wrapper
- i18n provider

### `frontend/src/i18n/index.ts`
- react-i18next configuration (ko default, en)
