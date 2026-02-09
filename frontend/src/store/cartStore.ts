import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { Menu, CartItem } from '../types'

interface CartState {
  items: CartItem[]
  totalAmount: number
  addToCart: (menu: Menu, quantity: number) => void
  updateQuantity: (menuId: number, quantity: number) => void
  removeItem: (menuId: number) => void
  clearCart: () => void
}

const calcTotal = (items: CartItem[]) =>
  items.reduce((sum, item) => sum + item.menu.price * item.quantity, 0)

export const useCartStore = create<CartState>()(
  persist(
    (set) => ({
      items: [],
      totalAmount: 0,
      addToCart: (menu: Menu, quantity: number) =>
        set((state) => {
          const existing = state.items.find((i) => i.menu.menuId === menu.menuId)
          if (existing) {
            if (existing.quantity >= 99) return state
            const items = state.items.map((i) =>
              i.menu.menuId === menu.menuId
                ? { ...i, quantity: Math.min(i.quantity + quantity, 99) }
                : i,
            )
            return { items, totalAmount: calcTotal(items) }
          }
          const items = [...state.items, { menu, quantity }]
          return { items, totalAmount: calcTotal(items) }
        }),
      updateQuantity: (menuId: number, quantity: number) =>
        set((state) => {
          if (quantity <= 0) {
            const items = state.items.filter((i) => i.menu.menuId !== menuId)
            return { items, totalAmount: calcTotal(items) }
          }
          const items = state.items.map((i) =>
            i.menu.menuId === menuId ? { ...i, quantity } : i,
          )
          return { items, totalAmount: calcTotal(items) }
        }),
      removeItem: (menuId: number) =>
        set((state) => {
          const items = state.items.filter((i) => i.menu.menuId !== menuId)
          return { items, totalAmount: calcTotal(items) }
        }),
      clearCart: () => set({ items: [], totalAmount: 0 }),
    }),
    { name: 'cart' },
  ),
)
