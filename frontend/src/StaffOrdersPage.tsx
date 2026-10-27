import { useEffect, useState } from 'react'
import {
  type OrderSummary,
  fetchStaffOrders,
  updateStaffOrderStatus,
} from './api'
import type { AuthSession } from './authTypes'

const NEXT: Record<string, string[]> = {
  NEW: ['SUBMITTED', 'CANCELLED'],
  SUBMITTED: ['IN_FULFILLMENT', 'CANCELLED'],
  IN_FULFILLMENT: ['COMPLETED', 'CANCELLED'],
  COMPLETED: [],
  CANCELLED: [],
}

function formatMoney(value: number) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(value)
}

type Props = {
  session: AuthSession | null
}

export function StaffOrdersPage({ session }: Props) {
  const [orders, setOrders] = useState<OrderSummary[]>([])
  const [error, setError] = useState<string | null>(null)

  async function reload(token: string) {
    const list = await fetchStaffOrders(token)
    setOrders(list)
  }

  useEffect(() => {
    if (!session || session.role !== 'STAFF') {
      setOrders([])
      return
    }
    reload(session.token).catch((err: unknown) => {
      setError(err instanceof Error ? err.message : 'Failed to load staff orders')
    })
  }, [session])

  if (!session) {
    return <p className="text-slate-600">Sign in as staff to manage orders.</p>
  }
  if (session.role !== 'STAFF') {
    return <p className="text-slate-600">Staff role required.</p>
  }
  if (error) {
    return <p className="text-red-700">{error}</p>
  }

  async function onAdvance(order: OrderSummary, status: string) {
    try {
      await updateStaffOrderStatus(session!.token, order.id, status)
      await reload(session!.token)
      setError(null)
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Status update failed')
    }
  }

  return (
    <ul className="space-y-4">
      {orders.map((order) => (
        <li
          key={order.id}
          className="rounded-xl bg-white/80 px-4 py-4 ring-1 ring-slate-200"
        >
          <div className="flex flex-wrap items-start justify-between gap-3">
            <div>
              <p className="font-medium text-slate-900">Order #{order.id}</p>
              <p className="text-sm text-slate-600">{order.customerEmail}</p>
              <p className="text-sm text-slate-600">{order.status}</p>
            </div>
            <p className="font-semibold text-sky-800">
              {formatMoney(Number(order.totalAmount))}
            </p>
          </div>
          <div className="mt-3 flex flex-wrap gap-2">
            {(NEXT[order.status] ?? []).map((status) => (
              <button
                key={status}
                type="button"
                className="rounded bg-slate-900 px-3 py-1.5 text-xs font-medium text-white"
                onClick={() => void onAdvance(order, status)}
              >
                → {status}
              </button>
            ))}
          </div>
        </li>
      ))}
    </ul>
  )
}
