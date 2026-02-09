// Domain Entities

export interface Menu {
  menuId: number
  menuName: string
  price: number
  description: string
  imageUrl: string
  categoryId: number
  storeId: number
}

export interface Category {
  categoryId: number
  categoryName: string
  storeId: number
}

export interface CartItem {
  menu: Menu
  quantity: number
}

export const OrderStatus = {
  PENDING: 'PENDING',
  PREPARING: 'PREPARING',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED',
} as const

export type OrderStatus = (typeof OrderStatus)[keyof typeof OrderStatus]

export interface OrderItem {
  orderItemId: number
  orderId: number
  menuId: number
  menuName: string
  quantity: number
  unitPrice: number
}

export interface Order {
  orderId: number
  orderNumber: string
  tableId: number
  sessionId: string
  totalAmount: number
  status: OrderStatus
  orderTime: string
  items: OrderItem[]
}

export interface TableSession {
  sessionId: string
  tableId: number
  startTime: string
  endTime: string | null
  isActive: boolean
}

export interface TableWithOrders {
  tableId: number
  tableNumber: string
  totalAmount: number
  orders: Order[]
}

// DTOs

export interface OrderRequestDto {
  tableId: number
  sessionId: string
  items: OrderItemRequestDto[]
  totalAmount: number
}

export interface OrderItemRequestDto {
  menuId: number
  quantity: number
  unitPrice: number
}

export interface TableLoginRequestDto {
  storeId: number
  tableNumber: string
  password: string
}

export interface TableLoginResponseDto {
  tableId: number
  sessionId: string
  storeId: number
  tableNumber: string
}

export interface AdminLoginRequestDto {
  storeId: number
  username: string
  password: string
}

export interface AdminLoginResponseDto {
  token: string
  refreshToken: string
  adminId: number
  username: string
  storeId: number
}

export interface OrderStatusUpdateDto {
  status: OrderStatus
}

export interface ErrorResponseDto {
  status: number
  message: string
  timestamp: string
  path: string
}

export interface ApiError {
  message: string
  status: number
}

// SSE Events

export interface SseEvent {
  type: 'new-order' | 'order-status-change' | 'order-deleted'
  data: unknown
}

export interface NewOrderEvent {
  type: 'new-order'
  data: Order
}

export interface OrderStatusChangeEvent {
  type: 'order-status-change'
  data: { orderId: number; newStatus: OrderStatus }
}

export interface OrderDeletedEvent {
  type: 'order-deleted'
  data: { orderId: number }
}
