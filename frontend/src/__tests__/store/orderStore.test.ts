import { describe, it, expect, beforeEach } from 'vitest'
import { useOrderStore } from '../../store/orderStore'
import { OrderStatus } from '../../types'
import type { Order, TableWithOrders } from '../../types'

const mockOrder: Order = {
  orderId: 1,
  orderNumber: 'ORD-001',
  tableId: 1,
  sessionId: 'sess-1',
  totalAmount: 15000,
  status: OrderStatus.PENDING,
  orderTime: '2026-02-09T12:00:00Z',
  items: [],
}

const mockTables: TableWithOrders[] = [
  { tableId: 1, tableNumber: '1', totalAmount: 15000, orders: [mockOrder] },
]

describe('OrderStore', () => {
  beforeEach(() => {
    useOrderStore.setState({ tables: [], customerOrders: [] })
  })

  // TC-STORE-010
  it('setTables - 테이블 목록 설정', () => {
    useOrderStore.getState().setTables(mockTables)
    expect(useOrderStore.getState().tables).toHaveLength(1)
    expect(useOrderStore.getState().tables[0].tableId).toBe(1)
  })

  // TC-STORE-011
  it('addOrder - 신규 주문 추가', () => {
    useOrderStore.getState().setTables(mockTables)
    const newOrder: Order = { ...mockOrder, orderId: 2, orderNumber: 'ORD-002', totalAmount: 8000 }
    useOrderStore.getState().addOrder(newOrder)
    const table = useOrderStore.getState().tables[0]
    expect(table.orders).toHaveLength(2)
    expect(table.totalAmount).toBe(23000)
  })

  // TC-STORE-012
  it('updateOrderStatus - 주문 상태 변경', () => {
    useOrderStore.getState().setTables(mockTables)
    useOrderStore.getState().updateOrderStatus(1, OrderStatus.PREPARING)
    const order = useOrderStore.getState().tables[0].orders[0]
    expect(order.status).toBe(OrderStatus.PREPARING)
  })

  // TC-STORE-013
  it('removeOrder - 주문 삭제', () => {
    const order2: Order = { ...mockOrder, orderId: 2, totalAmount: 8000 }
    useOrderStore.setState({
      tables: [{ tableId: 1, tableNumber: '1', totalAmount: 23000, orders: [mockOrder, order2] }],
    })
    useOrderStore.getState().removeOrder(1)
    const table = useOrderStore.getState().tables[0]
    expect(table.orders).toHaveLength(1)
    expect(table.totalAmount).toBe(8000)
  })
})
