import { describe, it, expect, beforeEach } from 'vitest'
import { useLoadingStore } from '../../store/loadingStore'

describe('LoadingStore', () => {
  beforeEach(() => {
    useLoadingStore.setState({ isLoading: false })
  })

  // TC-STORE-014
  it('setLoading - 로딩 상태 토글', () => {
    useLoadingStore.getState().setLoading(true)
    expect(useLoadingStore.getState().isLoading).toBe(true)
    useLoadingStore.getState().setLoading(false)
    expect(useLoadingStore.getState().isLoading).toBe(false)
  })
})
