import type { Order } from '../../types'
import { OrderStatus } from '../../types'

interface OrderCardProps {
  order: Order
  onCancel?: (orderId: number) => void
}

const statusLabel: Record<OrderStatus, string> = {
  [OrderStatus.PENDING]: '대기중',
  [OrderStatus.PREPARING]: '준비중',
  [OrderStatus.COMPLETED]: '완료',
  [OrderStatus.CANCELLED]: '취소됨',
}

export function OrderCard({ order, onCancel }: OrderCardProps) {
  return (
    <div className="rounded-lg border p-4">
      <div className="flex items-center justify-between">
        <span className="font-semibold">{order.orderNumber}</span>
        <span className="text-sm">{statusLabel[order.status]}</span>
      </div>
      <p className="mt-1 text-lg font-bold">{Number(order.totalAmount).toLocaleString()}원</p>
      {onCancel && order.status === OrderStatus.PENDING && (
        <button
          onClick={() => onCancel(order.id)}
          className="mt-2 rounded border border-red-500 px-3 py-1 text-red-500 min-h-[44px]"
        >
          취소
        </button>
      )}
    </div>
  )
}
