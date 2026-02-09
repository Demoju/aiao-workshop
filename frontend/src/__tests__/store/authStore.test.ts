import { describe, it, expect, beforeEach } from 'vitest'
import { useAuthStore } from '../../store/authStore'

describe('AuthStore', () => {
  beforeEach(() => {
    useAuthStore.setState({ isAuthenticated: false, user: null })
  })

  // TC-STORE-008
  it('setAuth - 인증 정보 설정', () => {
    useAuthStore.getState().setAuth({ role: 'customer', tableId: 1, sessionId: 'sess-1', storeId: 1 })
    const state = useAuthStore.getState()
    expect(state.isAuthenticated).toBe(true)
    expect(state.user?.role).toBe('customer')
    expect(state.user?.tableId).toBe(1)
  })

  // TC-STORE-009
  it('logout - 로그아웃', () => {
    useAuthStore.getState().setAuth({ role: 'admin', token: 'jwt-token', storeId: 1 })
    useAuthStore.getState().logout()
    const state = useAuthStore.getState()
    expect(state.isAuthenticated).toBe(false)
    expect(state.user).toBeNull()
  })
})
