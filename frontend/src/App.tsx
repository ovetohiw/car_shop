import { CatalogPage } from './CatalogPage'

function App() {
  return (
    <main className="mx-auto min-h-screen max-w-5xl px-6 py-12">
      <header className="mb-10">
        <p className="text-sm font-semibold uppercase tracking-[0.2em] text-sky-700">
          Car Shop
        </p>
        <h1 className="mt-3 text-4xl font-semibold tracking-tight text-slate-900">
          Catalog
        </h1>
        <p className="mt-3 max-w-2xl text-lg text-slate-600">
          Browse cars and parts from the live API. Configurator and orders come next.
        </p>
      </header>
      <CatalogPage />
    </main>
  )
}

export default App
