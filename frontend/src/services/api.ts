import axios from 'axios'
import { useAuthStore } from '../store/authStore'

export const apiClient = axios.create({
  baseURL: '/api',
})

export function setupInterceptors() {
  apiClient.interceptors.request.use((config) => {
    const { user } = useAuthStore.getState()
    if (user?.token) {
      config.headers.Authorization = `Bearer ${user.token}`
    }
    return config
  })

  apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        useAuthStore.getState().logout()
      }
      return Promise.reject(error)
    },
  )
}

setupInterceptors()
