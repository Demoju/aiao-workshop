import { describe, it, expect, beforeEach } from 'vitest'
import { useCartStore } from '../../store/cartStore'
import type { Menu } from '../../types'

const mockMenu: Menu = {
  menuId: 1,
  menuName: '김치찌개',
  price: 8000,
  description: '맛있는 김치찌개',
  imageUrl: '/images/kimchi.jpg',
  categoryId: 1,
  storeId: 1,
}

const mockMenu2: Menu = {
  menuId: 2,
  menuName: '된장찌개',
  price: 7000,
  description: '맛있는 된장찌개',
  imageUrl: '/images/doenjang.jpg',
  categoryId: 1,
  storeId: 1,
}

describe('CartStore', () => {
  beforeEach(() => {
    useCartStore.setState({ items: [], totalAmount: 0 })
  })

  // TC-STORE-001
  it('addToCart - 새 메뉴 추가', () => {
    useCartStore.getState().addToCart(mockMenu, 1)
    const state = useCartStore.getState()
    expect(state.items).toHaveLength(1)
    expect(state.items[0].menu.menuId).toBe(1)
    expect(state.items[0].quantity).toBe(1)
    expect(state.totalAmount).toBe(8000)
  })

  // TC-STORE-002
  it('addToCart - 동일 메뉴 추가 시 수량 증가', () => {
    useCartStore.getState().addToCart(mockMenu, 1)
    useCartStore.getState().addToCart(mockMenu, 1)
    const state = useCartStore.getState()
    expect(state.items).toHaveLength(1)
    expect(state.items[0].quantity).toBe(2)
    expect(state.totalAmount).toBe(16000)
  })

  // TC-STORE-003
  it('addToCart - 수량 99 초과 방지', () => {
    useCartStore.setState({
      items: [{ menu: mockMenu, quantity: 99 }],
      totalAmount: 99 * 8000,
    })
    useCartStore.getState().addToCart(mockMenu, 1)
    const state = useCartStore.getState()
    expect(state.items[0].quantity).toBe(99)
  })

  // TC-STORE-004
  it('updateQuantity - 수량 변경', () => {
    useCartStore.getState().addToCart(mockMenu, 1)
    useCartStore.getState().updateQuantity(1, 5)
    const state = useCartStore.getState()
    expect(state.items[0].quantity).toBe(5)
    expect(state.totalAmount).toBe(40000)
  })

  // TC-STORE-005
  it('updateQuantity - 수량 0이면 삭제', () => {
    useCartStore.getState().addToCart(mockMenu, 1)
    useCartStore.getState().updateQuantity(1, 0)
    const state = useCartStore.getState()
    expect(state.items).toHaveLength(0)
    expect(state.totalAmount).toBe(0)
  })

  // TC-STORE-006
  it('removeItem - 항목 삭제', () => {
    useCartStore.getState().addToCart(mockMenu, 1)
    useCartStore.getState().addToCart(mockMenu2, 2)
    useCartStore.getState().removeItem(1)
    const state = useCartStore.getState()
    expect(state.items).toHaveLength(1)
    expect(state.items[0].menu.menuId).toBe(2)
    expect(state.totalAmount).toBe(14000)
  })

  // TC-STORE-007
  it('clearCart - 장바구니 비우기', () => {
    useCartStore.getState().addToCart(mockMenu, 1)
    useCartStore.getState().addToCart(mockMenu2, 2)
    useCartStore.getState().clearCart()
    const state = useCartStore.getState()
    expect(state.items).toHaveLength(0)
    expect(state.totalAmount).toBe(0)
  })
})
