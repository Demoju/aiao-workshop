import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'

interface HeaderProps {
  title: string
  showBack?: boolean
  showCart?: boolean
}

export function Header({ title, showBack, showCart }: HeaderProps) {
  const navigate = useNavigate()
  const user = useAuthStore((s) => s.user)

  return (
    <header className="sticky top-0 z-10 flex items-center justify-between bg-white px-4 py-3 shadow">
      <div className="flex items-center gap-2">
        {showBack && (
          <button onClick={() => navigate(-1)} className="min-h-[44px] min-w-[44px]">←</button>
        )}
        <h1 className="text-lg font-bold">{title}</h1>
      </div>
      <div className="flex items-center gap-2">
        {showCart && user?.role === 'customer' && (
          <button onClick={() => navigate('/customer/cart')} className="min-h-[44px] min-w-[44px]">🛒</button>
        )}
      </div>
    </header>
  )
}
