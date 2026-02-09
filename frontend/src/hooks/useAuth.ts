import { useAuthStore } from '../store/authStore'
import * as customerApi from '../services/customerApi'
import * as adminApi from '../services/adminApi'
import type { TableLoginRequestDto, AdminLoginRequestDto } from '../types'

export function useAuth() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)
  const user = useAuthStore((s) => s.user)
  const setAuth = useAuthStore((s) => s.setAuth)
  const logoutAction = useAuthStore((s) => s.logout)

  const login = async (credentials: TableLoginRequestDto | AdminLoginRequestDto) => {
    if ('tableNumber' in credentials) {
      const res = await customerApi.tableLogin(credentials)
      setAuth({
        role: 'customer',
        tableId: res.tableId,
        sessionId: res.sessionId,
        storeId: res.storeId,
      })
    } else {
      const res = await adminApi.adminLogin(credentials)
      setAuth({
        role: 'admin',
        token: res.token,
      })
    }
  }

  const logout = () => logoutAction()

  return { isAuthenticated, user, login, logout }
}
