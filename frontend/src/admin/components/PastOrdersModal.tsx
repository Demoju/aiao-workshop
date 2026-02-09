import { useState, useEffect } from 'react'
import * as adminApi from '../../services/adminApi'
import type { Order } from '../../types'

interface PastOrdersModalProps {
  tableId: number
  onClose: () => void
}

export function PastOrdersModal({ tableId, onClose }: PastOrdersModalProps) {
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      const data = await adminApi.getPastOrders(tableId)
      setOrders(data)
      setLoading(false)
    }
    load()
  }, [tableId])

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="mx-4 w-full max-w-md rounded-lg bg-white p-6 max-h-[80vh] overflow-y-auto">
        <h2 className="text-lg font-bold">과거 주문 내역</h2>
        {loading ? (
          <p className="mt-4 text-center">로딩 중...</p>
        ) : orders.length === 0 ? (
          <p className="mt-4 text-center text-gray-500">과거 주문이 없습니다</p>
        ) : (
          <div className="mt-3 space-y-2">
            {orders.map((o) => (
              <div key={o.orderId} className="rounded border p-3 text-sm">
                <div className="flex justify-between">
                  <span>{o.orderNumber}</span>
                  <span>{o.totalAmount.toLocaleString()}원</span>
                </div>
              </div>
            ))}
          </div>
        )}
        <button onClick={onClose} className="mt-4 w-full rounded border py-2 min-h-[44px]">닫기</button>
      </div>
    </div>
  )
}
