import { useEffect, useState } from 'react'
import { type CarSummary, type Part, fetchCars, fetchParts } from './api'

function formatMoney(value: number) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 0,
  }).format(value)
}

export function CatalogPage() {
  const [cars, setCars] = useState<CarSummary[]>([])
  const [parts, setParts] = useState<Part[]>([])
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    Promise.all([fetchCars(), fetchParts()])
      .then(([carList, partList]) => {
        if (!cancelled) {
          setCars(carList)
          setParts(partList)
          setLoading(false)
        }
      })
      .catch((err: unknown) => {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : 'Failed to load catalog')
          setLoading(false)
        }
      })
    return () => {
      cancelled = true
    }
  }, [])

  if (loading) {
    return <p className="text-slate-600">Loading catalog…</p>
  }

  if (error) {
    return <p className="text-red-700">Catalog error: {error}</p>
  }

  return (
    <div className="space-y-12">
      <section>
        <h2 className="text-2xl font-semibold text-slate-900">Cars</h2>
        <p className="mt-1 text-slate-600">Seeded models with photos from the API.</p>
        <ul className="mt-6 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {cars.map((car) => (
            <li key={car.id} className="overflow-hidden rounded-xl bg-white/70 shadow-sm ring-1 ring-slate-200">
              <img
                src={car.imageUrl}
                alt={car.name}
                className="h-40 w-full object-cover"
              />
              <div className="space-y-1 p-4">
                <h3 className="font-medium text-slate-900">{car.name}</h3>
                <p className="text-sm text-slate-600">{car.description}</p>
                <p className="text-sm font-semibold text-sky-800">
                  from {formatMoney(Number(car.basePrice))}
                </p>
              </div>
            </li>
          ))}
        </ul>
      </section>

      <section>
        <h2 className="text-2xl font-semibold text-slate-900">Parts</h2>
        <p className="mt-1 text-slate-600">Spare parts available to order.</p>
        <ul className="mt-6 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {parts.map((part) => (
            <li key={part.id} className="overflow-hidden rounded-xl bg-white/70 shadow-sm ring-1 ring-slate-200">
              <img
                src={part.imageUrl}
                alt={part.name}
                className="h-36 w-full object-cover"
              />
              <div className="space-y-1 p-4">
                <h3 className="font-medium text-slate-900">{part.name}</h3>
                <p className="text-sm text-slate-600">{part.description}</p>
                <p className="text-sm font-semibold text-sky-800">
                  {formatMoney(Number(part.price))}
                </p>
              </div>
            </li>
          ))}
        </ul>
      </section>
    </div>
  )
}
