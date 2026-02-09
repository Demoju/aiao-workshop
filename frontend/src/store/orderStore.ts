import { create } from 'zustand'
import type { Order, OrderStatus, TableWithOrders } from '../types'

interface OrderState {
  tables: TableWithOrders[]
  customerOrders: Order[]
  setTables: (tables: TableWithOrders[]) => void
  setCustomerOrders: (orders: Order[]) => void
  addOrder: (order: Order) => void
  updateOrderStatus: (orderId: number, status: OrderStatus) => void
  removeOrder: (orderId: number) => void
}

export const useOrderStore = create<OrderState>()((set) => ({
  tables: [],
  customerOrders: [],
  setTables: (tables) => set({ tables }),
  setCustomerOrders: (orders) => set({ customerOrders: orders }),
  addOrder: (order) =>
    set((state) => ({
      tables: state.tables.map((t) =>
        t.tableId === order.tableId
          ? { ...t, orders: [...t.orders, order], totalAmount: t.totalAmount + order.totalAmount }
          : t,
      ),
    })),
  updateOrderStatus: (orderId, status) =>
    set((state) => ({
      tables: state.tables.map((t) => ({
        ...t,
        orders: t.orders.map((o) => (o.orderId === orderId ? { ...o, status } : o)),
      })),
    })),
  removeOrder: (orderId) =>
    set((state) => ({
      tables: state.tables.map((t) => {
        const removed = t.orders.find((o) => o.orderId === orderId)
        if (!removed) return t
        const orders = t.orders.filter((o) => o.orderId !== orderId)
        return { ...t, orders, totalAmount: t.totalAmount - removed.totalAmount }
      }),
    })),
}))
