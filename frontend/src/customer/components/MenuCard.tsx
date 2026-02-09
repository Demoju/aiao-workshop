import type { Menu } from '../../types'

interface MenuCardProps {
  menu: Menu
  onAddToCart: (menu: Menu, quantity: number) => void
}

export function MenuCard({ menu, onAddToCart }: MenuCardProps) {
  return (
    <div className="rounded-lg border p-4 shadow-sm">
      <img
        src={menu.imageUrl}
        alt={menu.menuName}
        className="mb-2 h-40 w-full rounded object-cover"
        loading="lazy"
      />
      <h3 className="text-lg font-semibold">{menu.menuName}</h3>
      <p className="text-sm text-gray-600">{menu.description}</p>
      <p className="mt-1 text-lg font-bold">{menu.price.toLocaleString()}원</p>
      <button
        onClick={() => onAddToCart(menu, 1)}
        className="mt-2 w-full rounded bg-blue-500 px-4 py-2 text-white min-h-[44px]"
      >
        담기
      </button>
    </div>
  )
}
