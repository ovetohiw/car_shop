const API_BASE = import.meta.env.VITE_API_BASE_URL ?? ''

type AuthHeaders = { Authorization?: string }

async function requestJson<T>(
  path: string,
  init?: RequestInit & { token?: string },
): Promise<T> {
  const headers: Record<string, string> = {
    ...(init?.headers as Record<string, string> | undefined),
  }
  if (init?.body && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json'
  }
  if (init?.token) {
    headers.Authorization = `Bearer ${init.token}`
  }
  const response = await fetch(`${API_BASE}${path}`, { ...init, headers })
  if (!response.ok) {
    let message = `Request failed: ${response.status}`
    try {
      const body = (await response.json()) as { error?: string }
      if (body.error) {
        message = body.error
      }
    } catch {
      // keep status message
    }
    throw new Error(message)
  }
  if (response.status === 204) {
    return undefined as T
  }
  return response.json() as Promise<T>
}

export type CarSummary = {
  id: number
  code: string
  name: string
  description: string
  basePrice: number
  imageUrl: string
}

export type CarDetail = CarSummary & {
  trims: Array<{
    id: number
    code: string
    name: string
    priceDelta: number
  }>
}

export type Part = {
  id: number
  sku: string
  name: string
  description: string
  price: number
  imageUrl: string
  stockQty: number
}

export type ColorOption = {
  id: number
  code: string
  name: string
  hexCode: string
  priceDelta: number
}

export type ExtraService = {
  id: number
  code: string
  name: string
  price: number
}

export type QuoteResponse = {
  totalAmount: number
  lines: Array<{
    itemType: string
    description: string
    quantity: number
    unitPrice: number
    lineTotal: number
  }>
}

export type AuthResponse = {
  accessToken: string
  tokenType: string
  expiresInSeconds: number
  email: string
  role: string
}

export type OrderRequest = {
  car?: {
    carModelId: number
    trimId: number
    colorId: number
    extraServiceIds: number[]
  }
  parts?: Array<{ partId: number; quantity: number }>
  claimedTotal?: number
}

export function fetchCars() {
  return requestJson<CarSummary[]>('/api/cars')
}

export function fetchCar(id: number) {
  return requestJson<CarDetail>(`/api/cars/${id}`)
}

export function fetchParts() {
  return requestJson<Part[]>('/api/parts')
}

export function fetchColors() {
  return requestJson<ColorOption[]>('/api/colors')
}

export function fetchExtraServices() {
  return requestJson<ExtraService[]>('/api/extra-services')
}

export function quoteOrder(body: OrderRequest) {
  return requestJson<QuoteResponse>('/api/orders/quote', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

export function createOrder(body: OrderRequest, token: string) {
  return requestJson<{ id: number; totalAmount: number; status: string }>(
    '/api/orders',
    {
      method: 'POST',
      body: JSON.stringify(body),
      token,
    },
  )
}

export function login(email: string, password: string) {
  return requestJson<AuthResponse>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  })
}

export function register(email: string, password: string) {
  return requestJson<AuthResponse>('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  })
}

export type OrderSummary = {
  id: number
  status: string
  totalAmount: number
  createdAt: string
  updatedAt: string
  customerEmail: string
}

export function fetchMyOrders(token: string) {
  return requestJson<OrderSummary[]>('/api/orders/mine', { token })
}

export function fetchStaffOrders(token: string) {
  return requestJson<OrderSummary[]>('/api/staff/orders', { token })
}

export function updateStaffOrderStatus(
  token: string,
  orderId: number,
  status: string,
) {
  return requestJson<OrderSummary>(`/api/staff/orders/${orderId}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status }),
    token,
  })
}

export type { AuthHeaders }
