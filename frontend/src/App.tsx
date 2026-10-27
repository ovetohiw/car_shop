import { useState } from 'react'
import { CatalogPage } from './CatalogPage'
import { ConfiguratorPage } from './ConfiguratorPage'

type Tab = 'catalog' | 'configure'

function App() {
  const [tab, setTab] = useState<Tab>('catalog')

  return (
    <main className="mx-auto min-h-screen max-w-5xl px-6 py-12">
      <header className="mb-10">
        <p className="text-sm font-semibold uppercase tracking-[0.2em] text-sky-700">
          Car Shop
        </p>
        <h1 className="mt-3 text-4xl font-semibold tracking-tight text-slate-900">
          {tab === 'catalog' ? 'Catalog' : 'Configurator'}
        </h1>
        <p className="mt-3 max-w-2xl text-lg text-slate-600">
          {tab === 'catalog'
            ? 'Browse cars and parts from the live API.'
            : 'Build a configuration and see the server-calculated total.'}
        </p>
        <div className="mt-6 flex gap-3">
          <button
            type="button"
            className={`rounded px-4 py-2 text-sm font-medium ${
              tab === 'catalog'
                ? 'bg-slate-900 text-white'
                : 'bg-white text-slate-700 ring-1 ring-slate-300'
            }`}
            onClick={() => setTab('catalog')}
          >
            Catalog
          </button>
          <button
            type="button"
            className={`rounded px-4 py-2 text-sm font-medium ${
              tab === 'configure'
                ? 'bg-slate-900 text-white'
                : 'bg-white text-slate-700 ring-1 ring-slate-300'
            }`}
            onClick={() => setTab('configure')}
          >
            Configurator
          </button>
        </div>
      </header>
      {tab === 'catalog' ? <CatalogPage /> : <ConfiguratorPage />}
    </main>
  )
}

export default App
