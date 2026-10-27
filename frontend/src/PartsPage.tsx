import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { type Part, fetchParts } from './api'
import { formatMoney } from './formatMoney'

export function PartsPage() {
  const navigate = useNavigate()
  const [parts, setParts] = useState<Part[]>([])
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    fetchParts()
      .then((partList) => {
        if (!cancelled) {
          setParts(partList)
          setLoading(false)
        }
      })
      .catch((err: unknown) => {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : 'Failed to load parts')
          setLoading(false)
        }
      })
    return () => {
      cancelled = true
    }
  }, [])

  if (loading) {
    return <p className="loading-state">Loading parts…</p>
  }

  if (error) {
    return <p className="error-state">Could not load parts. {error}</p>
  }

  return (
    <div>
      <header className="page-intro">
        <h1>Parts</h1>
        <p>Spare parts you can add when placing an order in the configurator.</p>
      </header>

      {parts.length === 0 ? (
        <p className="empty-state">No parts available.</p>
      ) : (
        <div className="product-grid">
          {parts.map((part, index) => (
            <button
              key={part.id}
              type="button"
              className="product"
              style={{ animationDelay: `${index * 60}ms` }}
              onClick={() => navigate(`/configure?part=${part.id}`)}
            >
              <img src={part.imageUrl} alt="" />
              <div className="product__body">
                <h2>{part.name}</h2>
                <p>{part.description}</p>
                <p className="product__price">{formatMoney(Number(part.price))}</p>
                <p>In stock: {part.stockQty}</p>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  )
}
