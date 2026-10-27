import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { type OrderSummary, fetchMyOrders } from './api'
import { useAuth } from './AuthContext'
import { formatMoney } from './formatMoney'

export function MyOrdersPage() {
  const { session } = useAuth()
  const [orders, setOrders] = useState<OrderSummary[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!session) {
      setOrders([])
      setLoading(false)
      return
    }
    setLoading(true)
    fetchMyOrders(session.token)
      .then((list) => {
        setOrders(list)
        setError(null)
      })
      .catch((err: unknown) => {
        setError(err instanceof Error ? err.message : 'Failed to load orders')
      })
      .finally(() => setLoading(false))
  }, [session])

  return (
    <div>
      <header className="page-intro">
        <h1>My orders</h1>
        <p>Track every order placed with your customer account.</p>
      </header>

      {!session ? (
        <p className="empty-state">Sign in above to see your orders.</p>
      ) : null}
      {session && loading ? <p className="loading-state">Loading orders…</p> : null}
      {error ? <p className="error-state">{error}</p> : null}
      {session && !loading && !error && orders.length === 0 ? (
        <p className="empty-state">
          No orders yet.{' '}
          <Link to="/configure" style={{ color: 'var(--accent-deep)', fontWeight: 700 }}>
            Open the configurator
          </Link>
          .
        </p>
      ) : null}

      {orders.length > 0 ? (
        <ul className="order-list">
          {orders.map((order) => (
            <li key={order.id} className="panel order-row">
              <div>
                <h2>Order #{order.id}</h2>
                <p>{order.status}</p>
                <p>
                  Updated {new Date(order.updatedAt).toLocaleString()}
                </p>
              </div>
              <p className="product__price">
                {formatMoney(Number(order.totalAmount))}
              </p>
            </li>
          ))}
        </ul>
      ) : null}
    </div>
  )
}
