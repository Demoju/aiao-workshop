import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface AuthUser {
  role: 'customer' | 'admin'
  tableId?: number
  sessionId?: string
  token?: string
  storeId?: number
  username?: string
}

interface AuthState {
  isAuthenticated: boolean
  user: AuthUser | null
  setAuth: (user: AuthUser) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      isAuthenticated: false,
      user: null,
      setAuth: (user: AuthUser) => set({ isAuthenticated: true, user }),
      logout: () => set({ isAuthenticated: false, user: null }),
    }),
    {
      name: 'auth',
      storage: {
        getItem: (name) => {
          const v = sessionStorage.getItem(name)
          return v ? JSON.parse(v) : null
        },
        setItem: (name, value) => sessionStorage.setItem(name, JSON.stringify(value)),
        removeItem: (name) => sessionStorage.removeItem(name),
      },
    },
  ),
)
