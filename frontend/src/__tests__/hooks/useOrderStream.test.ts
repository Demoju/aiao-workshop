import { describe, it, expect, beforeEach, vi } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { useOrderStream } from '../../hooks/useOrderStream'
import { useOrderStore } from '../../store/orderStore'

let autoOpen = true

class MockEventSource {
  static instances: MockEventSource[] = []
  url: string
  onopen: (() => void) | null = null
  onerror: (() => void) | null = null
  listeners: Record<string, ((e: { data: string }) => void)[]> = {}
  readyState = 0

  constructor(url: string) {
    this.url = url
    MockEventSource.instances.push(this)
    if (autoOpen) {
      setTimeout(() => this.onopen?.(), 0)
    }
  }

  addEventListener(type: string, cb: (e: { data: string }) => void) {
    if (!this.listeners[type]) this.listeners[type] = []
    this.listeners[type].push(cb)
  }

  close() { this.readyState = 2 }

  simulateError() { this.onerror?.() }
}

describe('useOrderStream', () => {
  beforeEach(() => {
    MockEventSource.instances = []
    autoOpen = true
    vi.stubGlobal('EventSource', MockEventSource)
    useOrderStore.setState({ tables: [], customerOrders: [] })
  })

  // TC-HOOK-004
  it('SSE 연결 및 이벤트 처리', async () => {
    const { result } = renderHook(() => useOrderStream('test-token'))
    await act(async () => { await new Promise((r) => setTimeout(r, 10)) })
    expect(result.current.isConnected).toBe(true)
    expect(MockEventSource.instances[0].url).toContain('token=test-token')
  })

  // TC-HOOK-005
  it('재연결 max 3회 후 error', async () => {
    autoOpen = false
    const { result } = renderHook(() => useOrderStream('test-token'))
    await act(async () => { await new Promise((r) => setTimeout(r, 10)) })

    // Error on initial + 3 retries = 4 total
    for (let i = 0; i < 4; i++) {
      await act(async () => {
        const instance = MockEventSource.instances[MockEventSource.instances.length - 1]
        instance.simulateError()
        await new Promise((r) => setTimeout(r, 10))
      })
    }
    expect(result.current.isConnected).toBe(false)
    expect(result.current.error).toBeTruthy()
  })
})
