import type { TableWithOrders } from '../../types'
import { OrderStatus } from '../../types'

interface TableCardProps {
  table: TableWithOrders
  onEndSession: (tableId: number) => void
  onOrderClick: (orderId: number) => void
}

export function TableCard({ table, onEndSession, onOrderClick }: TableCardProps) {
  const activeOrders = table.orders.filter((o) => o.status !== OrderStatus.CANCELLED)

  return (
    <div className="rounded-lg border p-4">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-bold">테이블 {table.tableNumber}</h3>
        <button
          onClick={() => onEndSession(table.tableId)}
          className="rounded border border-red-500 px-3 py-1 text-sm text-red-500 min-h-[44px]"
        >
          이용 완료
        </button>
      </div>
      <p className="mt-1 font-semibold">{table.totalAmount.toLocaleString()}원</p>
      <div className="mt-2 space-y-1">
        {activeOrders.map((order) => (
          <button
            key={order.orderId}
            onClick={() => onOrderClick(order.orderId)}
            className="w-full rounded bg-gray-50 p-2 text-left text-sm min-h-[44px]"
          >
            {order.orderNumber} - {order.status} - {order.totalAmount.toLocaleString()}원
          </button>
        ))}
      </div>
    </div>
  )
}
