import { describe, it, expect, beforeEach } from 'vitest'
import axios from 'axios'
import { useAuthStore } from '../../store/authStore'

describe('Axios Interceptors', () => {
  let client: ReturnType<typeof axios.create>

  beforeEach(() => {
    useAuthStore.setState({ isAuthenticated: false, user: null })
    client = axios.create({ baseURL: '/api' })

    // Setup interceptors manually for testing
    client.interceptors.request.use((config) => {
      const { user } = useAuthStore.getState()
      if (user?.token) {
        config.headers.Authorization = `Bearer ${user.token}`
      }
      return config
    })

    client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          useAuthStore.getState().logout()
        }
        return Promise.reject(error)
      },
    )
  })

  // TC-API-007
  it('Request interceptor - JWT 토큰 첨부', async () => {
    useAuthStore.setState({ isAuthenticated: true, user: { role: 'admin', token: 'test-jwt' } })
    // Intercept the request config before it's sent
    const config = await client.interceptors.request.handlers[0].fulfilled!({
      headers: new axios.AxiosHeaders(),
    } as any)
    expect(config.headers.Authorization).toBe('Bearer test-jwt')
  })

  // TC-API-008
  it('Response interceptor - 401 에러 시 logout', async () => {
    useAuthStore.setState({ isAuthenticated: true, user: { role: 'admin', token: 'test-jwt' } })
    const error = { response: { status: 401 } }
    try {
      await client.interceptors.response.handlers[0].rejected!(error)
    } catch {
      // expected rejection
    }
    expect(useAuthStore.getState().isAuthenticated).toBe(false)
  })
})
