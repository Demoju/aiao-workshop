import type { AdminLoginRequestDto, AdminLoginResponseDto, Order, OrderStatusUpdateDto, TableWithOrders } from '../types'
import { apiClient } from './api'

export const adminLogin = async (dto: AdminLoginRequestDto): Promise<AdminLoginResponseDto> => {
  const { data } = await apiClient.post('/admin/login', dto)
  return data
}

export const getOrders = async (): Promise<TableWithOrders[]> => {
  const { data } = await apiClient.get('/admin/orders')
  return data
}

export const updateOrderStatus = async (orderId: number, dto: OrderStatusUpdateDto): Promise<void> => {
  await apiClient.patch(`/admin/orders/${orderId}/status`, dto)
}

export const deleteOrder = async (orderId: number): Promise<void> => {
  await apiClient.delete(`/admin/orders/${orderId}`)
}

export const endTableSession = async (tableId: number): Promise<void> => {
  await apiClient.post(`/admin/tables/${tableId}/end-session`)
}

export const getPastOrders = async (tableId: number): Promise<Order[]> => {
  const { data } = await apiClient.get(`/admin/tables/${tableId}/past-orders`)
  return data
}
