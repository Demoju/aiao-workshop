import type { CartItem as CartItemType } from '../../types'

interface CartItemProps {
  item: CartItemType
  onUpdateQuantity: (menuId: number, quantity: number) => void
  onRemove: (menuId: number) => void
}

export function CartItem({ item, onUpdateQuantity, onRemove }: CartItemProps) {
  return (
    <div className="flex items-center justify-between border-b py-3">
      <div className="flex-1">
        <h4 className="font-semibold">{item.menu.menuName}</h4>
        <p className="text-sm text-gray-600">{item.menu.price.toLocaleString()}원</p>
      </div>
      <div className="flex items-center gap-2">
        <button
          onClick={() => onUpdateQuantity(item.menu.menuId, item.quantity - 1)}
          className="h-8 w-8 rounded border min-h-[44px] min-w-[44px] flex items-center justify-center"
        >
          -
        </button>
        <span className="w-8 text-center">{item.quantity}</span>
        <button
          onClick={() => onUpdateQuantity(item.menu.menuId, item.quantity + 1)}
          className="h-8 w-8 rounded border min-h-[44px] min-w-[44px] flex items-center justify-center"
        >
          +
        </button>
        <button
          onClick={() => onRemove(item.menu.menuId)}
          className="ml-2 text-red-500 min-h-[44px] min-w-[44px]"
        >
          ✕
        </button>
      </div>
    </div>
  )
}
