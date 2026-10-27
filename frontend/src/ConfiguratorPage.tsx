import { useEffect, useMemo, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import {
  type CarDetail,
  type CarSummary,
  type ColorOption,
  type ExtraService,
  type Part,
  createOrder,
  fetchCar,
  fetchCars,
  fetchColors,
  fetchExtraServices,
  fetchParts,
  quoteOrder,
} from './api'
import { useAuth } from './AuthContext'
import { formatMoney } from './formatMoney'

export function ConfiguratorPage() {
  const { session } = useAuth()
  const [searchParams] = useSearchParams()
  const presetCar = Number(searchParams.get('car') ?? '')
  const presetPart = Number(searchParams.get('part') ?? '')

  const [cars, setCars] = useState<CarSummary[]>([])
  const [carDetail, setCarDetail] = useState<CarDetail | null>(null)
  const [colors, setColors] = useState<ColorOption[]>([])
  const [extras, setExtras] = useState<ExtraService[]>([])
  const [parts, setParts] = useState<Part[]>([])

  const [carModelId, setCarModelId] = useState<number | ''>('')
  const [trimId, setTrimId] = useState<number | ''>('')
  const [colorId, setColorId] = useState<number | ''>('')
  const [extraIds, setExtraIds] = useState<number[]>([])
  const [partId, setPartId] = useState<number | ''>('')
  const [partQty, setPartQty] = useState(1)

  const [serverTotal, setServerTotal] = useState<number | null>(null)
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    Promise.all([fetchCars(), fetchColors(), fetchExtraServices(), fetchParts()])
      .then(([carList, colorList, extraList, partList]) => {
        setCars(carList)
        setColors(colorList)
        setExtras(extraList)
        setParts(partList)
        const initialCar =
          Number.isFinite(presetCar) && carList.some((car) => car.id === presetCar)
            ? presetCar
            : carList[0]?.id
        if (initialCar) {
          setCarModelId(initialCar)
        }
        if (colorList[0]) {
          setColorId(colorList[0].id)
        }
        const initialPart =
          Number.isFinite(presetPart) && partList.some((part) => part.id === presetPart)
            ? presetPart
            : partList[0]?.id
        if (initialPart) {
          setPartId(initialPart)
        }
      })
      .catch((err: unknown) => {
        setError(err instanceof Error ? err.message : 'Failed to load options')
      })
  }, [presetCar, presetPart])

  useEffect(() => {
    if (carModelId === '') {
      return
    }
    fetchCar(carModelId)
      .then((detail) => {
        setCarDetail(detail)
        setTrimId(detail.trims[0]?.id ?? '')
      })
      .catch((err: unknown) => {
        setError(err instanceof Error ? err.message : 'Failed to load car')
      })
  }, [carModelId])

  const orderBody = useMemo(() => {
    if (carModelId === '' || trimId === '' || colorId === '') {
      return null
    }
    return {
      car: {
        carModelId,
        trimId,
        colorId,
        extraServiceIds: extraIds,
      },
      parts: partId === '' ? [] : [{ partId, quantity: partQty }],
    }
  }, [carModelId, trimId, colorId, extraIds, partId, partQty])

  useEffect(() => {
    if (!orderBody) {
      return
    }
    const handle = window.setTimeout(() => {
      quoteOrder(orderBody)
        .then((quote) => {
          setServerTotal(Number(quote.totalAmount))
          setError(null)
        })
        .catch((err: unknown) => {
          setServerTotal(null)
          setError(err instanceof Error ? err.message : 'Quote failed')
        })
    }, 250)
    return () => window.clearTimeout(handle)
  }, [orderBody])

  function toggleExtra(id: number) {
    setExtraIds((current) =>
      current.includes(id) ? current.filter((item) => item !== id) : [...current, id],
    )
  }

  async function onPlaceOrder() {
    if (!orderBody || !session) {
      setError('Sign in to place an order')
      return
    }
    try {
      const order = await createOrder(orderBody, session.token)
      setMessage(
        `Order #${order.id} created for ${formatMoney(Number(order.totalAmount))}`,
      )
      setError(null)
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Order failed')
    }
  }

  return (
    <div style={{ display: 'grid', gap: '1.25rem' }}>
      <header className="page-intro">
        <h1>Configurator</h1>
        <p>Build a configuration. The total comes only from the server.</p>
      </header>

      <section className="panel config-grid">
        <label>
          Car model
          <select
            className="field"
            value={carModelId}
            onChange={(event) => setCarModelId(Number(event.target.value))}
          >
            {cars.map((car) => (
              <option key={car.id} value={car.id}>
                {car.name}
              </option>
            ))}
          </select>
        </label>
        <label>
          Trim
          <select
            className="field"
            value={trimId}
            onChange={(event) => setTrimId(Number(event.target.value))}
          >
            {(carDetail?.trims ?? []).map((trim) => (
              <option key={trim.id} value={trim.id}>
                {trim.name} (+{formatMoney(Number(trim.priceDelta))})
              </option>
            ))}
          </select>
        </label>
        <label>
          Color
          <select
            className="field"
            value={colorId}
            onChange={(event) => setColorId(Number(event.target.value))}
          >
            {colors.map((color) => (
              <option key={color.id} value={color.id}>
                {color.name} (+{formatMoney(Number(color.priceDelta))})
              </option>
            ))}
          </select>
        </label>
        <label>
          Part
          <select
            className="field"
            value={partId}
            onChange={(event) => setPartId(Number(event.target.value))}
          >
            {parts.map((part) => (
              <option key={part.id} value={part.id}>
                {part.name} ({formatMoney(Number(part.price))})
              </option>
            ))}
          </select>
        </label>
        <label>
          Part quantity
          <input
            className="field"
            type="number"
            min={1}
            value={partQty}
            onChange={(event) => setPartQty(Number(event.target.value))}
          />
        </label>
      </section>

      <section className="panel">
        <h2 style={{ margin: 0, fontFamily: 'var(--font-display)', fontSize: '1.1rem' }}>
          Extra services
        </h2>
        <ul className="extras" style={{ marginTop: '0.75rem' }}>
          {extras.map((extra) => (
            <li key={extra.id}>
              <label>
                <input
                  type="checkbox"
                  checked={extraIds.includes(extra.id)}
                  onChange={() => toggleExtra(extra.id)}
                />
                {extra.name} ({formatMoney(Number(extra.price))})
              </label>
            </li>
          ))}
        </ul>
      </section>

      <section className="panel total-panel">
        <p className="eyebrow">Server total</p>
        <p className="amount">
          {serverTotal == null ? '—' : formatMoney(serverTotal)}
        </p>
        <p style={{ margin: '0.5rem 0 0', color: 'var(--muted)', fontSize: '0.9rem' }}>
          Client totals are ignored or rejected.
        </p>
      </section>

      <button
        type="button"
        className="btn btn--accent"
        disabled={!session || serverTotal == null}
        onClick={() => void onPlaceOrder()}
      >
        Place order
      </button>

      {!session ? (
        <p className="empty-state">Sign in above to place an order.</p>
      ) : null}
      {message ? <p className="form-ok">{message}</p> : null}
      {error ? <p className="form-error">{error}</p> : null}
    </div>
  )
}
