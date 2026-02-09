import { useEffect, useRef, useState, useCallback } from 'react'
import { useOrderStore } from '../store/orderStore'
import type { Order, OrderStatus } from '../types'

const MAX_RETRIES = 3

export function useOrderStream(token: string) {
  const [isConnected, setIsConnected] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const retriesRef = useRef(0)
  const esRef = useRef<EventSource | null>(null)
  const addOrder = useOrderStore((s) => s.addOrder)
  const updateOrderStatus = useOrderStore((s) => s.updateOrderStatus)
  const removeOrder = useOrderStore((s) => s.removeOrder)

  const connect = useCallback(() => {
    if (esRef.current) esRef.current.close()

    const es = new EventSource(`/api/admin/orders/stream?token=${token}`)
    esRef.current = es

    es.onopen = () => {
      setIsConnected(true)
      setError(null)
      retriesRef.current = 0
    }

    es.addEventListener('new-order', (e) => {
      const order: Order = JSON.parse(e.data)
      addOrder(order)
    })

    es.addEventListener('order-status-change', (e) => {
      const { orderId, newStatus }: { orderId: number; newStatus: OrderStatus } = JSON.parse(e.data)
      updateOrderStatus(orderId, newStatus)
    })

    es.addEventListener('order-deleted', (e) => {
      const { orderId }: { orderId: number } = JSON.parse(e.data)
      removeOrder(orderId)
    })

    es.onerror = () => {
      es.close()
      setIsConnected(false)
      if (retriesRef.current < MAX_RETRIES) {
        retriesRef.current++
        connect()
      } else {
        setError('연결이 끊어졌습니다. 새로고침해주세요')
      }
    }
  }, [token, addOrder, updateOrderStatus, removeOrder])

  const reconnect = useCallback(() => {
    retriesRef.current = 0
    setError(null)
    connect()
  }, [connect])

  useEffect(() => {
    connect()
    return () => { esRef.current?.close() }
  }, [connect])

  return { isConnected, error, reconnect }
}
