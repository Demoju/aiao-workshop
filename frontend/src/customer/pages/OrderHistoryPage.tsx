import { useState, useEffect } from 'react'
import { Header } from '../../shared/Header'
import { OrderCard } from '../components/OrderCard'
import { useAuthStore } from '../../store/authStore'
import * as customerApi from '../../services/customerApi'
import type { Order } from '../../types'

export default function OrderHistoryPage() {
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(true)
  const user = useAuthStore((s) => s.user)

  useEffect(() => {
    if (!user?.tableId || !user?.sessionId) return
    const load = async () => {
      setLoading(true)
      const data = await customerApi.getOrders(user.tableId!, user.sessionId!)
      setOrders(data.sort((a, b) => new Date(b.orderTime).getTime() - new Date(a.orderTime).getTime()))
      setLoading(false)
    }
    load()
  }, [user?.tableId, user?.sessionId])

  const handleCancel = async (orderId: number) => {
    await customerApi.cancelOrder(orderId)
    setOrders((prev) => prev.filter((o) => o.orderId !== orderId))
  }

  if (loading) return <div className="p-4 text-center">로딩 중...</div>

  return (
    <div>
      <Header title="주문 내역" showBack showCart />
      <div className="space-y-3 p-4">
        {orders.length === 0 ? (
          <p className="text-center text-gray-500">주문 내역이 없습니다</p>
        ) : (
          orders.map((order) => (
            <OrderCard key={order.orderId} order={order} onCancel={handleCancel} />
          ))
        )}
      </div>
    </div>
  )
}
