import { render, screen } from '@testing-library/react'
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
}))

describe('App', () => {
  it('shows Car Shop brand and catalog sections', async () => {
    render(<App />)
    expect(screen.getByText('Car Shop')).toBeInTheDocument()
    expect(await screen.findByText('Aura Sedan')).toBeInTheDocument()
    expect(await screen.findByText('Ceramic brake pads')).toBeInTheDocument()
  })
})
