import type { AdminLoginRequestDto, AdminLoginResponseDto, Order } from '../types'
import { apiClient } from './api'
import type { OrderStatus } from '../types'

export const adminLogin = async (dto: AdminLoginRequestDto): Promise<AdminLoginResponseDto> => {
  const { data } = await apiClient.post('/admin/login', dto)
  return data
}

// Backend returns flat OrderResponseDto[], storeId required as query param
export const getOrders = async (storeId: number): Promise<Order[]> => {
  const { data } = await apiClient.get('/admin/orders', { params: { storeId } })
  return data
}

// Backend expects status as @RequestParam, not body
export const updateOrderStatus = async (orderId: number, status: OrderStatus): Promise<void> => {
  await apiClient.patch(`/admin/orders/${orderId}/status`, null, { params: { status } })
}

export const deleteOrder = async (orderId: number): Promise<void> => {
  await apiClient.delete(`/admin/orders/${orderId}`)
}

// NOTE: endTableSession, getPastOrders, SSE not implemented in Backend yet
