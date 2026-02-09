import { describe, it, expect, beforeEach, vi } from 'vitest'
import { renderHook } from '@testing-library/react'
import { useAuth } from '../../hooks/useAuth'
import { useAuthStore } from '../../store/authStore'

vi.mock('../../services/customerApi', () => ({
  tableLogin: vi.fn().mockResolvedValue({ tableId: 1, sessionId: 's1', storeId: 1, tableNumber: '1' }),
}))

vi.mock('../../services/adminApi', () => ({
  adminLogin: vi.fn().mockResolvedValue({ token: 'jwt', refreshToken: 'rt', adminId: 1, username: 'admin', storeId: 1 }),
}))

describe('useAuth', () => {
  beforeEach(() => {
    useAuthStore.setState({ isAuthenticated: false, user: null })
  })

  // TC-HOOK-002
  it('테이블 로그인 플로우', async () => {
    const { result } = renderHook(() => useAuth())
    await result.current.login({ storeId: 1, tableNumber: '1', password: 'pass' })
    expect(useAuthStore.getState().isAuthenticated).toBe(true)
    expect(useAuthStore.getState().user?.role).toBe('customer')
    expect(useAuthStore.getState().user?.tableId).toBe(1)
  })

  // TC-HOOK-003
  it('관리자 로그인 플로우', async () => {
    const { result } = renderHook(() => useAuth())
    await result.current.login({ storeId: 1, username: 'admin', password: 'pass' })
    expect(useAuthStore.getState().isAuthenticated).toBe(true)
    expect(useAuthStore.getState().user?.role).toBe('admin')
    expect(useAuthStore.getState().user?.token).toBe('jwt')
  })
})
