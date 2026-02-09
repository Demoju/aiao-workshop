import type { Order } from '../../types'
import { OrderStatus } from '../../types'

interface OrderCardProps {
  order: Order
  onStatusChange: (orderId: number, status: OrderStatus) => void
  onDelete: (orderId: number) => void
}

const statusLabel: Record<OrderStatus, string> = {
  [OrderStatus.PENDING]: '대기중',
  [OrderStatus.PREPARING]: '준비중',
  [OrderStatus.COMPLETED]: '완료',
  [OrderStatus.CANCELLED]: '취소됨',
}

const statusColor: Record<OrderStatus, string> = {
  [OrderStatus.PENDING]: 'bg-yellow-100 text-yellow-800',
  [OrderStatus.PREPARING]: 'bg-blue-100 text-blue-800',
  [OrderStatus.COMPLETED]: 'bg-green-100 text-green-800',
  [OrderStatus.CANCELLED]: 'bg-gray-100 text-gray-500',
}

const nextStatus: Partial<Record<OrderStatus, OrderStatus>> = {
  [OrderStatus.PENDING]: OrderStatus.PREPARING,
  [OrderStatus.PREPARING]: OrderStatus.COMPLETED,
}

export function AdminOrderCard({ order, onStatusChange, onDelete }: OrderCardProps) {
  const next = nextStatus[order.status]

  return (
    <div className="rounded-lg border bg-white p-4 shadow-sm">
      <div className="flex items-center justify-between">
        <span className="font-semibold">{order.orderNumber}</span>
        <span className={`rounded-full px-2 py-1 text-xs font-medium ${statusColor[order.status]}`}>
          {statusLabel[order.status]}
        </span>
      </div>
      <div className="mt-2 text-sm text-gray-600">
        <p>테이블 ID: {order.tableId}</p>
        <p>금액: {Number(order.totalAmount).toLocaleString()}원</p>
        <p>시간: {new Date(order.createdAt).toLocaleTimeString('ko-KR')}</p>
      </div>
      <div className="mt-3 flex gap-2">
        {next && (
          <button
            onClick={() => onStatusChange(order.id, next)}
            className="flex-1 rounded bg-blue-500 py-2 text-sm text-white min-h-[44px]"
          >
            {next === OrderStatus.PREPARING ? '수락' : '완료 처리'}
          </button>
        )}
        {(order.status === OrderStatus.COMPLETED || order.status === OrderStatus.CANCELLED) && (
          <button
            onClick={() => onDelete(order.id)}
            className="rounded border border-red-500 px-3 py-2 text-sm text-red-500 min-h-[44px]"
          >
            삭제
          </button>
        )}
        {order.status === OrderStatus.PENDING && (
          <button
            onClick={() => onStatusChange(order.id, OrderStatus.CANCELLED)}
            className="rounded border border-gray-400 px-3 py-2 text-sm text-gray-600 min-h-[44px]"
          >
            거절
          </button>
        )}
      </div>
    </div>
  )
}
