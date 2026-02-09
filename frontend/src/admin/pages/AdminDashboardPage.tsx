import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { AdminOrderCard } from '../components/AdminOrderCard'
import { ConfirmDialog } from '../../shared/ConfirmDialog'
import { useOrderStore } from '../../store/orderStore'
import { useAuthStore } from '../../store/authStore'
import * as adminApi from '../../services/adminApi'
import type { OrderStatus } from '../../types'

export default function AdminDashboardPage() {
  const orders = useOrderStore((s) => s.orders)
  const setOrders = useOrderStore((s) => s.setOrders)
  const user = useAuthStore((s) => s.user)
  const logout = useAuthStore((s) => s.logout)
  const navigate = useNavigate()
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [confirmAction, setConfirmAction] = useState<{ message: string; action: () => void } | null>(null)

  useEffect(() => {
    if (!user?.token) {
      navigate('/admin/login')
      return
    }
    const load = async () => {
      try {
        // Backend requires storeId - using 1 as default
        const data = await adminApi.getOrders(1)
        setOrders(data)
      } catch (err) {
        setError('주문 목록을 불러올 수 없습니다')
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [setOrders, user?.token, navigate])

  const handleStatusChange = (orderId: number, status: OrderStatus) => {
    setConfirmAction({
      message: `주문 상태를 변경하시겠습니까?`,
      action: async () => {
        try {
          await adminApi.updateOrderStatus(orderId, status)
          useOrderStore.getState().updateOrderStatus(orderId, status)
        } catch {
          setError('상태 변경에 실패했습니다')
        }
        setConfirmAction(null)
      },
    })
  }

  const handleDelete = (orderId: number) => {
    setConfirmAction({
      message: '주문을 삭제하시겠습니까?',
      action: async () => {
        try {
          await adminApi.deleteOrder(orderId)
          useOrderStore.getState().removeOrder(orderId)
        } catch {
          setError('삭제에 실패했습니다')
        }
        setConfirmAction(null)
      },
    })
  }

  const handleRefresh = async () => {
    setLoading(true)
    setError('')
    try {
      const data = await adminApi.getOrders(1)
      setOrders(data)
    } catch {
      setError('주문 목록을 불러올 수 없습니다')
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/admin/login')
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="sticky top-0 z-10 flex items-center justify-between bg-white px-4 py-3 shadow">
        <h1 className="text-lg font-bold">주문 관리</h1>
        <div className="flex gap-2">
          <button onClick={handleRefresh} className="rounded border px-3 py-1 text-sm min-h-[44px]">새로고침</button>
          <button onClick={handleLogout} className="rounded border border-red-500 px-3 py-1 text-sm text-red-500 min-h-[44px]">로그아웃</button>
        </div>
      </header>

      {error && <div className="bg-red-100 p-2 text-center text-sm text-red-600">{error}</div>}

      {loading ? (
        <div className="p-8 text-center text-gray-500">로딩 중...</div>
      ) : orders.length === 0 ? (
        <div className="p-8 text-center text-gray-500">주문이 없습니다</div>
      ) : (
        <div className="grid gap-4 p-4 md:grid-cols-2 lg:grid-cols-3">
          {orders.map((order) => (
            <AdminOrderCard
              key={order.id}
              order={order}
              onStatusChange={handleStatusChange}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}

      {confirmAction && (
        <ConfirmDialog
          isOpen
          title="확인"
          message={confirmAction.message}
          onConfirm={confirmAction.action}
          onCancel={() => setConfirmAction(null)}
        />
      )}
    </div>
  )
}
