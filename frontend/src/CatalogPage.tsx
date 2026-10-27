import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { type CarSummary, fetchCars } from './api'
import { formatMoneyCompact } from './formatMoney'

export function CatalogPage() {
  const navigate = useNavigate()
  const [cars, setCars] = useState<CarSummary[]>([])
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    fetchCars()
      .then((carList) => {
        if (!cancelled) {
          setCars(carList)
          setLoading(false)
        }
      })
      .catch((err: unknown) => {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : 'Failed to load cars')
          setLoading(false)
        }
      })
    return () => {
      cancelled = true
    }
  }, [])

  if (loading) {
    return <p className="loading-state">Loading cars…</p>
  }

  if (error) {
    return <p className="error-state">Could not load cars. {error}</p>
  }

  const hero = cars[0]

  return (
    <div>
      {hero ? (
        <section className="hero" aria-label="Featured car">
          <img className="hero__media" src={hero.imageUrl} alt="" />
          <div className="hero__veil" />
          <div className="hero__copy">
            <p className="brand">Car Shop</p>
            <p className="hero__lead">{hero.name}</p>
            <p>{hero.description}</p>
            <Link className="btn btn--accent" to={`/configure?car=${hero.id}`}>
              Configure from {formatMoneyCompact(Number(hero.basePrice))}
            </Link>
          </div>
        </section>
      ) : null}

      <header className="page-intro">
        <h1>Cars</h1>
        <p>Pick a model and open the configurator for trims, color, and extras.</p>
      </header>

      {cars.length === 0 ? (
        <p className="empty-state">No cars in the catalog yet.</p>
      ) : (
        <div className="product-grid">
          {cars.map((car, index) => (
            <button
              key={car.id}
              type="button"
              className="product"
              style={{ animationDelay: `${index * 60}ms` }}
              onClick={() => navigate(`/configure?car=${car.id}`)}
            >
              <img src={car.imageUrl} alt="" />
              <div className="product__body">
                <h2>{car.name}</h2>
                <p>{car.description}</p>
                <p className="product__price">
                  from {formatMoneyCompact(Number(car.basePrice))}
                </p>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  )
}
