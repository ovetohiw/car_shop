import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import App from './App'

vi.mock('./api', () => ({
  fetchCars: () =>
    Promise.resolve([
      {
        id: 1,
        code: 'SEDAN-AURA',
        name: 'Aura Sedan',
        description: 'Test car',
        basePrice: 24990,
        imageUrl: '/images/cars/aura-sedan.svg',
      },
    ]),
  fetchCar: () =>
    Promise.resolve({
      id: 1,
      code: 'SEDAN-AURA',
      name: 'Aura Sedan',
      description: 'Test car',
      basePrice: 24990,
      imageUrl: '/images/cars/aura-sedan.svg',
      trims: [{ id: 1, code: 'BASE', name: 'Base', priceDelta: 0 }],
    }),
  fetchParts: () =>
    Promise.resolve([
      {
        id: 1,
        sku: 'BRK-PAD-01',
        name: 'Ceramic brake pads',
        description: 'Test part',
        price: 129.9,
        imageUrl: '/images/parts/brake-pads.svg',
        stockQty: 40,
      },
    ]),
  fetchColors: () =>
    Promise.resolve([
      {
        id: 1,
        code: 'WHITE',
        name: 'Arctic White',
        hexCode: '#fff',
        priceDelta: 0,
      },
    ]),
  fetchExtraServices: () =>
    Promise.resolve([
      {
        id: 1,
        code: 'WARR',
        name: 'Warranty',
        price: 100,
      },
    ]),
  quoteOrder: () =>
    Promise.resolve({
      totalAmount: 25090,
      lines: [],
    }),
  createOrder: vi.fn(),
  login: vi.fn(),
  register: vi.fn(),
}))

describe('App', () => {
  it('shows catalog and can open configurator with server total', async () => {
    const user = userEvent.setup()
    render(<App />)
    expect(screen.getByText('Car Shop')).toBeInTheDocument()
    expect(await screen.findByText('Aura Sedan')).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: 'Configurator' }))
    expect(await screen.findByText('Server total')).toBeInTheDocument()
    expect(await screen.findByText('$25,090.00')).toBeInTheDocument()
  })
})
