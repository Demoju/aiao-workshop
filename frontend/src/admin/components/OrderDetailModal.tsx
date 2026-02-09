import type { Order } from '../../types'
import { OrderStatus } from '../../types'

interface OrderDetailModalProps {
  order: Order
  onStatusChange: (orderId: number, status: OrderStatus) => void
  onDelete: (orderId: number) => void
  onClose: () => void
}

const nextStatus: Partial<Record<OrderStatus, OrderStatus>> = {
  [OrderStatus.PENDING]: OrderStatus.PREPARING,
  [OrderStatus.PREPARING]: OrderStatus.COMPLETED,
}

export function OrderDetailModal({ order, onStatusChange, onDelete, onClose }: OrderDetailModalProps) {
  const next = nextStatus[order.status]

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="mx-4 w-full max-w-md rounded-lg bg-white p-6">
        <h2 className="text-lg font-bold">{order.orderNumber}</h2>
        <div className="mt-3 space-y-1">
          {order.items.map((item) => (
            <div key={item.orderItemId} className="flex justify-between text-sm">
              <span>{item.menuName} x{item.quantity}</span>
              <span>{(item.unitPrice * item.quantity).toLocaleString()}원</span>
            </div>
          ))}
        </div>
        <p className="mt-3 text-lg font-bold">{order.totalAmount.toLocaleString()}원</p>
        <div className="mt-4 flex gap-2">
          {next && (
            <button onClick={() => onStatusChange(order.orderId, next)} className="flex-1 rounded bg-blue-500 py-2 text-white min-h-[44px]">
              {next === OrderStatus.PREPARING ? '수락' : '완료'}
            </button>
          )}
          <button onClick={() => onDelete(order.orderId)} className="rounded border border-red-500 px-4 py-2 text-red-500 min-h-[44px]">삭제</button>
          <button onClick={onClose} className="rounded border px-4 py-2 min-h-[44px]">닫기</button>
        </div>
      </div>
    </div>
  )
}
