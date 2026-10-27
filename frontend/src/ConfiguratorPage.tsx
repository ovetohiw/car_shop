import { useEffect, useMemo, useState } from 'react'
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
  login,
  quoteOrder,
  register,
} from './api'

function formatMoney(value: number) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(value)
}

export function ConfiguratorPage() {
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

  const [token, setToken] = useState<string | null>(null)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
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
        if (carList[0]) {
          setCarModelId(carList[0].id)
        }
        if (colorList[0]) {
          setColorId(colorList[0].id)
        }
        if (partList[0]) {
          setPartId(partList[0].id)
        }
      })
      .catch((err: unknown) => {
        setError(err instanceof Error ? err.message : 'Failed to load options')
      })
  }, [])

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
      parts:
        partId === ''
          ? []
          : [{ partId, quantity: partQty }],
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

  async function onAuth(mode: 'login' | 'register') {
    try {
      const result =
        mode === 'login'
          ? await login(email, password)
          : await register(email, password)
      setToken(result.accessToken)
      setMessage(`Signed in as ${result.email}`)
      setError(null)
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Auth failed')
    }
  }

  async function onPlaceOrder() {
    if (!orderBody || !token) {
      setError('Sign in to place an order')
      return
    }
    try {
      const order = await createOrder(orderBody, token)
      setMessage(`Order #${order.id} created for ${formatMoney(Number(order.totalAmount))}`)
      setError(null)
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Order failed')
    }
  }

  return (
    <div className="space-y-8">
      <section className="grid gap-4 md:grid-cols-2">
        <label className="block text-sm text-slate-700">
          Car model
          <select
            className="mt-1 w-full rounded border border-slate-300 bg-white px-3 py-2"
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
        <label className="block text-sm text-slate-700">
          Trim
          <select
            className="mt-1 w-full rounded border border-slate-300 bg-white px-3 py-2"
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
        <label className="block text-sm text-slate-700">
          Color
          <select
            className="mt-1 w-full rounded border border-slate-300 bg-white px-3 py-2"
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
        <label className="block text-sm text-slate-700">
          Part
          <select
            className="mt-1 w-full rounded border border-slate-300 bg-white px-3 py-2"
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
        <label className="block text-sm text-slate-700">
          Part quantity
          <input
            type="number"
            min={1}
            className="mt-1 w-full rounded border border-slate-300 bg-white px-3 py-2"
            value={partQty}
            onChange={(event) => setPartQty(Number(event.target.value))}
          />
        </label>
      </section>

      <section>
        <h3 className="text-lg font-medium text-slate-900">Extra services</h3>
        <ul className="mt-3 space-y-2">
          {extras.map((extra) => (
            <li key={extra.id}>
              <label className="flex items-center gap-2 text-sm text-slate-700">
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

      <section className="rounded-xl bg-sky-50 px-4 py-5 ring-1 ring-sky-100">
        <p className="text-sm uppercase tracking-wide text-sky-800">Server total</p>
        <p className="mt-1 text-3xl font-semibold text-slate-900">
          {serverTotal == null ? '—' : formatMoney(serverTotal)}
        </p>
        <p className="mt-2 text-sm text-slate-600">
          Price is calculated only on the backend. Client totals are ignored or rejected.
        </p>
      </section>

      <section className="grid gap-3 md:grid-cols-[1fr_1fr_auto_auto]">
        <input
          className="rounded border border-slate-300 px-3 py-2"
          placeholder="email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
        />
        <input
          className="rounded border border-slate-300 px-3 py-2"
          placeholder="password"
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
        />
        <button
          type="button"
          className="rounded bg-slate-900 px-4 py-2 text-white"
          onClick={() => void onAuth('login')}
        >
          Sign in
        </button>
        <button
          type="button"
          className="rounded bg-slate-700 px-4 py-2 text-white"
          onClick={() => void onAuth('register')}
        >
          Register
        </button>
      </section>

      <button
        type="button"
        className="rounded bg-sky-700 px-5 py-3 font-medium text-white disabled:opacity-50"
        disabled={!token || serverTotal == null}
        onClick={() => void onPlaceOrder()}
      >
        Place order
      </button>

      {message ? <p className="text-sm text-emerald-700">{message}</p> : null}
      {error ? <p className="text-sm text-red-700">{error}</p> : null}
    </div>
  )
}
