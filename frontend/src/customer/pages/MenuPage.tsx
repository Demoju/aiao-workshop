import { useState, useEffect } from 'react'
import { Header } from '../../shared/Header'
import { MenuCard } from '../components/MenuCard'
import { useCartStore } from '../../store/cartStore'
import { useAuthStore } from '../../store/authStore'
import * as customerApi from '../../services/customerApi'
import type { Menu, Category } from '../../types'

export default function MenuPage() {
  const [menus, setMenus] = useState<Menu[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [selectedCategory, setSelectedCategory] = useState<number | null>(null)
  const [loading, setLoading] = useState(true)
  const addToCart = useCartStore((s) => s.addToCart)
  const user = useAuthStore((s) => s.user)

  useEffect(() => {
    if (!user?.storeId) return
    const load = async () => {
      setLoading(true)
      const [m, c] = await Promise.all([
        customerApi.getMenus(user.storeId!),
        customerApi.getCategories(user.storeId!),
      ])
      setMenus(m)
      setCategories(c)
      setLoading(false)
    }
    load()
  }, [user?.storeId])

  const filtered = selectedCategory
    ? menus.filter((m) => m.categoryId === selectedCategory)
    : menus

  if (loading) return <div className="p-4 text-center">로딩 중...</div>

  return (
    <div>
      <Header title="메뉴" showCart />
      <div className="flex gap-2 overflow-x-auto p-4">
        <button
          onClick={() => setSelectedCategory(null)}
          className={`shrink-0 rounded-full px-4 py-2 min-h-[44px] ${!selectedCategory ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
        >
          전체
        </button>
        {categories.map((c) => (
          <button
            key={c.categoryId}
            onClick={() => setSelectedCategory(c.categoryId)}
            className={`shrink-0 rounded-full px-4 py-2 min-h-[44px] ${selectedCategory === c.categoryId ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
          >
            {c.categoryName}
          </button>
        ))}
      </div>
      <div className="grid grid-cols-2 gap-4 p-4">
        {filtered.map((menu) => (
          <MenuCard key={menu.menuId} menu={menu} onAddToCart={addToCart} />
        ))}
      </div>
    </div>
  )
}
