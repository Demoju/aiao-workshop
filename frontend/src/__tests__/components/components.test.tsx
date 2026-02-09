import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { MenuCard } from '../../customer/components/MenuCard'
import { CartItem } from '../../customer/components/CartItem'
import { OrderCard } from '../../customer/components/OrderCard'
import { OrderStatus } from '../../types'
import type { Menu, CartItem as CartItemType, Order } from '../../types'

const mockMenu: Menu = {
  menuId: 1, menuName: '김치찌개', price: 8000,
  description: '맛있는 김치찌개', imageUrl: '/img.jpg', categoryId: 1, storeId: 1,
}

describe('MenuCard', () => {
  // TC-COMP-001
  it('메뉴 정보 렌더링', () => {
    render(<MenuCard menu={mockMenu} onAddToCart={vi.fn()} />)
    expect(screen.getByText('김치찌개')).toBeInTheDocument()
    expect(screen.getByText('8,000원')).toBeInTheDocument()
    expect(screen.getByText('맛있는 김치찌개')).toBeInTheDocument()
  })

  // TC-COMP-002
  it('장바구니 추가 버튼 클릭', () => {
    const onAdd = vi.fn()
    render(<MenuCard menu={mockMenu} onAddToCart={onAdd} />)
    fireEvent.click(screen.getByText('담기'))
    expect(onAdd).toHaveBeenCalledWith(mockMenu, 1)
  })
})

describe('CartItem', () => {
  const item: CartItemType = { menu: mockMenu, quantity: 2 }

  // TC-COMP-003
  it('수량 증가/감소 버튼', () => {
    const onUpdate = vi.fn()
    const onRemove = vi.fn()
    render(<CartItem item={item} onUpdateQuantity={onUpdate} onRemove={onRemove} />)
    fireEvent.click(screen.getByText('+'))
    expect(onUpdate).toHaveBeenCalledWith(1, 3)
    fireEvent.click(screen.getByText('-'))
    expect(onUpdate).toHaveBeenCalledWith(1, 1)
  })
})

describe('OrderCard', () => {
  const order: Order = {
    orderId: 1, orderNumber: 'ORD-001', tableId: 1, sessionId: 's1',
    totalAmount: 15000, status: OrderStatus.PENDING, orderTime: '2026-02-09T12:00:00Z', items: [],
  }

  // TC-COMP-004
  it('주문 정보 렌더링', () => {
    render(<OrderCard order={order} />)
    expect(screen.getByText('ORD-001')).toBeInTheDocument()
    expect(screen.getByText('대기중')).toBeInTheDocument()
    expect(screen.getByText('15,000원')).toBeInTheDocument()
  })
})
