import { useState, useEffect } from 'react'
import { Header } from '../../shared/Header'
import { TableCard } from '../components/TableCard'
import { OrderDetailModal } from '../components/OrderDetailModal'
import { ConfirmDialog } from '../../shared/ConfirmDialog'
import { useOrderStore } from '../../store/orderStore'
import { useAuthStore } from '../../store/authStore'
import { useOrderStream } from '../../hooks/useOrderStream'
import * as adminApi from '../../services/adminApi'
import type { Order } from '../../types'
import { OrderStatus } from '../../types'

export default function AdminDashboardPage() {
  const tables = useOrderStore((s) => s.tables)
  const setTables = useOrderStore((s) => s.setTables)
  const user = useAuthStore((s) => s.user)
  const { error: sseError } = useOrderStream(user?.token || '')
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null)
  const [confirmAction, setConfirmAction] = useState<{ message: string; action: () => void } | null>(null)

  useEffect(() => {
    const load = async () => {
      const data = await adminApi.getOrders()
      setTables(data)
    }
    load()
  }, [setTables])

  const handleStatusChange = async (orderId: number, status: OrderStatus) => {
    await adminApi.updateOrderStatus(orderId, { status })
    useOrderStore.getState().updateOrderStatus(orderId, status)
    setSelectedOrder(null)
  }

  const handleDelete = (orderId: number) => {
    setConfirmAction({
      message: '주문을 삭제하시겠습니까?',
      action: async () => {
        await adminApi.deleteOrder(orderId)
        useOrderStore.getState().removeOrder(orderId)
        setSelectedOrder(null)
        setConfirmAction(null)
      },
    })
  }

  const handleEndSession = (tableId: number) => {
    setConfirmAction({
      message: '테이블 이용을 완료하시겠습니까?',
      action: async () => {
        await adminApi.endTableSession(tableId)
        setTables(tables.map((t) => t.tableId === tableId ? { ...t, orders: [], totalAmount: 0 } : t))
        setConfirmAction(null)
      },
    })
  }

  const handleOrderClick = (orderId: number) => {
    const order = tables.flatMap((t) => t.orders).find((o) => o.orderId === orderId)
    if (order) setSelectedOrder(order)
  }

  return (
    <div>
      <Header title="주문 관리" />
      {sseError && <div className="bg-red-100 p-2 text-center text-sm text-red-600">{sseError}</div>}
      <div className="grid gap-4 p-4 md:grid-cols-2 lg:grid-cols-3">
        {tables.map((table) => (
          <TableCard key={table.tableId} table={table} onEndSession={handleEndSession} onOrderClick={handleOrderClick} />
        ))}
      </div>
      {selectedOrder && (
        <OrderDetailModal order={selectedOrder} onStatusChange={handleStatusChange} onDelete={handleDelete} onClose={() => setSelectedOrder(null)} />
      )}
      {confirmAction && (
        <ConfirmDialog isOpen title="확인" message={confirmAction.message} onConfirm={confirmAction.action} onCancel={() => setConfirmAction(null)} />
      )}
    </div>
  )
}
