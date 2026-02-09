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

// Backend OrderResponseDto 매핑
export interface Order {
  id: number
  tableId: number
  orderNumber: string
  status: OrderStatus
  totalAmount: number
  createdAt: string
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
  storeId: number
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

// Backend: { username, password } only
export interface AdminLoginRequestDto {
  username: string
  password: string
}

// Backend returns token only
export interface AdminLoginResponseDto {
  token: string
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
