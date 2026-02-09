import { describe, it, expect, beforeEach } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { useCart } from '../../hooks/useCart'
import { useCartStore } from '../../store/cartStore'
import type { Menu } from '../../types'

const mockMenu: Menu = {
  menuId: 1, menuName: '김치찌개', price: 8000,
  description: '', imageUrl: '', categoryId: 1, storeId: 1,
}

describe('useCart', () => {
  beforeEach(() => {
    useCartStore.setState({ items: [], totalAmount: 0 })
  })

  // TC-HOOK-001
  it('CartStore 연동 확인', () => {
    useCartStore.getState().addToCart(mockMenu, 2)
    const { result } = renderHook(() => useCart())
    expect(result.current.items).toHaveLength(1)
    expect(result.current.totalAmount).toBe(16000)
  })
})
