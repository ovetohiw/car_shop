import { useEffect, useState } from 'react'
import { type OrderSummary, fetchMyOrders } from './api'
import type { AuthSession } from './authTypes'

function formatMoney(value: number) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(value)
}

type Props = {
  session: AuthSession | null
}

export function MyOrdersPage({ session }: Props) {
  const [orders, setOrders] = useState<OrderSummary[]>([])
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!session) {
      setOrders([])
      return
    }
    fetchMyOrders(session.token)
      .then(setOrders)
      .catch((err: unknown) => {
        setError(err instanceof Error ? err.message : 'Failed to load orders')
      })
  }, [session])

  if (!session) {
    return <p className="text-slate-600">Sign in to see your orders.</p>
  }

  if (error) {
    return <p className="text-red-700">{error}</p>
  }

  if (orders.length === 0) {
    return <p className="text-slate-600">No orders yet.</p>
  }

  return (
    <ul className="space-y-3">
      {orders.map((order) => (
        <li
          key={order.id}
          className="flex flex-wrap items-center justify-between gap-3 rounded-xl bg-white/80 px-4 py-3 ring-1 ring-slate-200"
        >
          <div>
            <p className="font-medium text-slate-900">Order #{order.id}</p>
            <p className="text-sm text-slate-600">{order.status}</p>
          </div>
          <p className="font-semibold text-sky-800">
            {formatMoney(Number(order.totalAmount))}
          </p>
        </li>
      ))}
    </ul>
  )
}
