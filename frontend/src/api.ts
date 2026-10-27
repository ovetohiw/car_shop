const API_BASE = import.meta.env.VITE_API_BASE_URL ?? ''

async function getJson<T>(path: string): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`)
  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`)
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

export function fetchCars() {
  return getJson<CarSummary[]>('/api/cars')
}

export function fetchCar(id: number) {
  return getJson<CarDetail>(`/api/cars/${id}`)
}

export function fetchParts() {
  return getJson<Part[]>('/api/parts')
}
