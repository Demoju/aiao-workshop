import { useNavigate } from 'react-router-dom'
import { Header } from '../../shared/Header'
import { CartItem } from '../components/CartItem'
import { useCartStore } from '../../store/cartStore'
import { useAuthStore } from '../../store/authStore'
import * as customerApi from '../../services/customerApi'
import { useState } from 'react'

export default function CartPage() {
  const navigate = useNavigate()
  const { items, totalAmount, updateQuantity, removeItem, clearCart } = useCartStore()
  const user = useAuthStore((s) => s.user)
  const [submitting, setSubmitting] = useState(false)

  const handleOrder = async () => {
    if (items.length === 0 || !user?.tableId || !user?.sessionId) return
    setSubmitting(true)
    try {
      await customerApi.createOrder({
        tableId: user.tableId,
        sessionId: user.sessionId,
        items: items.map((i) => ({ menuId: i.menu.menuId, quantity: i.quantity, unitPrice: i.menu.price })),
        totalAmount,
      })
      clearCart()
      navigate('/customer/menu')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div>
      <Header title="장바구니" showBack />
      <div className="p-4">
        {items.length === 0 ? (
          <p className="text-center text-gray-500">장바구니가 비어있습니다</p>
        ) : (
          <>
            {items.map((item) => (
              <CartItem key={item.menu.menuId} item={item} onUpdateQuantity={updateQuantity} onRemove={removeItem} />
            ))}
            <div className="mt-4 flex items-center justify-between border-t pt-4">
              <span className="text-lg font-bold">합계</span>
              <span className="text-lg font-bold">{totalAmount.toLocaleString()}원</span>
            </div>
            <button
              onClick={handleOrder}
              disabled={submitting}
              className="mt-4 w-full rounded bg-blue-500 py-3 text-white min-h-[44px] disabled:opacity-50"
            >
              {submitting ? '주문 중...' : '주문하기'}
            </button>
          </>
        )}
      </div>
    </div>
  )
}
