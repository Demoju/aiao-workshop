import { useCartStore } from '../store/cartStore'

export function useCart() {
  const items = useCartStore((s) => s.items)
  const totalAmount = useCartStore((s) => s.totalAmount)
  const addToCart = useCartStore((s) => s.addToCart)
  const updateQuantity = useCartStore((s) => s.updateQuantity)
  const removeItem = useCartStore((s) => s.removeItem)
  const clearCart = useCartStore((s) => s.clearCart)

  return { items, totalAmount, addToCart, updateQuantity, removeItem, clearCart }
}
