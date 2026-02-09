import { describe, it, expect, beforeEach, vi } from 'vitest'
import { apiClient } from '../../services/api'
import * as customerApi from '../../services/customerApi'
import * as adminApi from '../../services/adminApi'
import { OrderStatus } from '../../types'

vi.mock('../../services/api', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
    patch: vi.fn(),
    delete: vi.fn(),
  },
  setupInterceptors: vi.fn(),
}))

describe('Customer API', () => {
  beforeEach(() => { vi.clearAllMocks() })

  // TC-API-001
  it('getMenus - 메뉴 목록 조회 성공', async () => {
    const menus = [{ menuId: 1, menuName: '김치찌개', price: 8000 }]
    vi.mocked(apiClient.get).mockResolvedValue({ data: menus })
    const result = await customerApi.getMenus(1)
    expect(result).toEqual(menus)
    expect(apiClient.get).toHaveBeenCalledWith('/customer/menus', { params: { storeId: 1 } })
  })

  // TC-API-002
  it('createOrder - 주문 생성 성공', async () => {
    const order = { orderId: 1, orderNumber: 'ORD-001' }
    const dto = { tableId: 1, sessionId: 's1', items: [], totalAmount: 8000 }
    vi.mocked(apiClient.post).mockResolvedValue({ data: order })
    const result = await customerApi.createOrder(dto)
    expect(result).toEqual(order)
    expect(apiClient.post).toHaveBeenCalledWith('/customer/orders', dto)
  })

  // TC-API-003
  it('cancelOrder - 주문 취소 성공', async () => {
    vi.mocked(apiClient.delete).mockResolvedValue({})
    await customerApi.cancelOrder(1)
    expect(apiClient.delete).toHaveBeenCalledWith('/customer/orders/1')
  })

  // TC-API-004
  it('tableLogin - 테이블 로그인 성공', async () => {
    const response = { tableId: 1, sessionId: 's1', storeId: 1, tableNumber: '1' }
    const dto = { storeId: 1, tableNumber: '1', password: 'pass' }
    vi.mocked(apiClient.post).mockResolvedValue({ data: response })
    const result = await customerApi.tableLogin(dto)
    expect(result).toEqual(response)
  })
})

describe('Admin API', () => {
  beforeEach(() => { vi.clearAllMocks() })

  // TC-API-005
  it('adminLogin - 관리자 로그인 성공', async () => {
    const response = { token: 'jwt', refreshToken: 'rt', adminId: 1, username: 'admin', storeId: 1 }
    vi.mocked(apiClient.post).mockResolvedValue({ data: response })
    const result = await adminApi.adminLogin({ storeId: 1, username: 'admin', password: 'pass' })
    expect(result).toEqual(response)
  })

  // TC-API-006
  it('updateOrderStatus - 주문 상태 변경 성공', async () => {
    vi.mocked(apiClient.patch).mockResolvedValue({})
    await adminApi.updateOrderStatus(1, { status: OrderStatus.PREPARING })
    expect(apiClient.patch).toHaveBeenCalledWith('/admin/orders/1/status', { status: 'PREPARING' })
  })
})
