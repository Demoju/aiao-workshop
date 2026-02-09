import type { Menu, Category, Order, OrderRequestDto, TableLoginRequestDto, TableLoginResponseDto } from '../types'
import { apiClient } from './api'

export const getMenus = async (_storeId: number): Promise<Menu[]> => {
  const { data } = await apiClient.get(`/customer/menus`, { params: { storeId: _storeId } })
  return data
}

export const getCategories = async (_storeId: number): Promise<Category[]> => {
  const { data } = await apiClient.get(`/customer/categories`, { params: { storeId: _storeId } })
  return data
}

export const createOrder = async (dto: OrderRequestDto): Promise<Order> => {
  const { data } = await apiClient.post('/customer/orders', dto)
  return data
}

export const getOrders = async (tableId: number, sessionId: string): Promise<Order[]> => {
  const { data } = await apiClient.get('/customer/orders', { params: { tableId, sessionId } })
  return data
}

export const cancelOrder = async (orderId: number): Promise<void> => {
  await apiClient.delete(`/customer/orders/${orderId}`)
}

export const tableLogin = async (dto: TableLoginRequestDto): Promise<TableLoginResponseDto> => {
  const { data } = await apiClient.post('/customer/login', dto)
  return data
}
