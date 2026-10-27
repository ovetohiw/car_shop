import { useEffect, useState } from 'react'
import {
  type OrderSummary,
  fetchStaffOrders,
  updateStaffOrderStatus,
} from './api'
import { useAuth } from './AuthContext'
import { formatMoney } from './formatMoney'

const NEXT: Record<string, string[]> = {
  NEW: ['SUBMITTED', 'CANCELLED'],
  SUBMITTED: ['IN_FULFILLMENT', 'CANCELLED'],
  IN_FULFILLMENT: ['COMPLETED', 'CANCELLED'],
  COMPLETED: [],
  CANCELLED: [],
}

export function StaffOrdersPage() {
  const { session } = useAuth()
  const [orders, setOrders] = useState<OrderSummary[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function reload(token: string) {
    const list = await fetchStaffOrders(token)
    setOrders(list)
  }

  useEffect(() => {
    if (!session || session.role !== 'STAFF') {
      setOrders([])
      setLoading(false)
      return
    }
    setLoading(true)
    reload(session.token)
      .then(() => setError(null))
      .catch((err: unknown) => {
        setError(err instanceof Error ? err.message : 'Failed to load staff orders')
      })
      .finally(() => setLoading(false))
  }, [session])

  async function onAdvance(order: OrderSummary, status: string) {
    if (!session) {
      return
    }
    try {
      await updateStaffOrderStatus(session.token, order.id, status)
      await reload(session.token)
      setError(null)
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Status update failed')
    }
  }

  return (
    <div>
      <header className="page-intro">
        <h1>Staff cabinet</h1>
        <p>Advance fulfillment statuses for every order in the shop.</p>
      </header>

      {!session ? (
        <p className="empty-state">Sign in as staff to manage orders.</p>
      ) : null}
      {session && session.role !== 'STAFF' ? (
        <p className="empty-state">
          Staff role required. Server still rejects non-staff calls.
        </p>
      ) : null}
      {session?.role === 'STAFF' && loading ? (
        <p className="loading-state">Loading orders…</p>
      ) : null}
      {error ? <p className="error-state">{error}</p> : null}
      {session?.role === 'STAFF' && !loading && !error && orders.length === 0 ? (
        <p className="empty-state">No orders yet.</p>
      ) : null}

      {orders.length > 0 ? (
        <ul className="order-list">
          {orders.map((order) => (
            <li key={order.id} className="panel">
              <div className="order-row">
                <div>
                  <h2>Order #{order.id}</h2>
                  <p>{order.customerEmail}</p>
                  <p>{order.status}</p>
                </div>
                <p className="product__price">
                  {formatMoney(Number(order.totalAmount))}
                </p>
              </div>
              <div className="status-actions">
                {(NEXT[order.status] ?? []).map((status) => (
                  <button
                    key={status}
                    type="button"
                    className="btn btn--solid btn--tiny"
                    onClick={() => void onAdvance(order, status)}
                  >
                    → {status}
                  </button>
                ))}
              </div>
            </li>
          ))}
        </ul>
      ) : null}
    </div>
  )
}
