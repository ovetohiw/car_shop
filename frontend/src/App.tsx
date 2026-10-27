import { useState } from 'react'
import { login, register } from './api'
import type { AuthSession } from './authTypes'
import { CatalogPage } from './CatalogPage'
import { ConfiguratorPage } from './ConfiguratorPage'
import { MyOrdersPage } from './MyOrdersPage'
import { StaffOrdersPage } from './StaffOrdersPage'

type Tab = 'catalog' | 'configure' | 'orders' | 'staff'

const TITLES: Record<Tab, { heading: string; blurb: string }> = {
  catalog: {
    heading: 'Catalog',
    blurb: 'Browse cars and parts from the live API.',
  },
  configure: {
    heading: 'Configurator',
    blurb: 'Build a configuration and see the server-calculated total.',
  },
  orders: {
    heading: 'My orders',
    blurb: 'Track orders placed with your customer account.',
  },
  staff: {
    heading: 'Staff cabinet',
    blurb: 'Advance fulfillment statuses for every order.',
  },
}

function App() {
  const [tab, setTab] = useState<Tab>('catalog')
  const [session, setSession] = useState<AuthSession | null>(null)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [authError, setAuthError] = useState<string | null>(null)

  async function onAuth(mode: 'login' | 'register') {
    try {
      const result =
        mode === 'login'
          ? await login(email, password)
          : await register(email, password)
      setSession({
        token: result.accessToken,
        email: result.email,
        role: result.role,
      })
      setAuthError(null)
    } catch (err: unknown) {
      setAuthError(err instanceof Error ? err.message : 'Auth failed')
    }
  }

  function signOut() {
    setSession(null)
  }

  const meta = TITLES[tab]

  return (
    <main className="mx-auto min-h-screen max-w-5xl px-6 py-12">
      <header className="mb-10">
        <p className="text-sm font-semibold uppercase tracking-[0.2em] text-sky-700">
          Car Shop
        </p>
        <h1 className="mt-3 text-4xl font-semibold tracking-tight text-slate-900">
          {meta.heading}
        </h1>
        <p className="mt-3 max-w-2xl text-lg text-slate-600">{meta.blurb}</p>

        <div className="mt-6 flex flex-wrap gap-3">
          {(
            [
              ['catalog', 'Catalog'],
              ['configure', 'Configurator'],
              ['orders', 'My orders'],
              ['staff', 'Staff'],
            ] as const
          ).map(([id, label]) => (
            <button
              key={id}
              type="button"
              className={`rounded px-4 py-2 text-sm font-medium ${
                tab === id
                  ? 'bg-slate-900 text-white'
                  : 'bg-white text-slate-700 ring-1 ring-slate-300'
              }`}
              onClick={() => setTab(id)}
            >
              {label}
            </button>
          ))}
        </div>

        <section className="mt-6 grid gap-3 md:grid-cols-[1fr_1fr_auto_auto_auto]">
          {session ? (
            <>
              <p className="md:col-span-2 self-center text-sm text-slate-700">
                Signed in as <span className="font-medium">{session.email}</span>{' '}
                ({session.role})
              </p>
              <button
                type="button"
                className="rounded bg-slate-700 px-4 py-2 text-white"
                onClick={signOut}
              >
                Sign out
              </button>
            </>
          ) : (
            <>
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
            </>
          )}
        </section>
        {authError ? <p className="mt-2 text-sm text-red-700">{authError}</p> : null}
      </header>

      {tab === 'catalog' ? <CatalogPage /> : null}
      {tab === 'configure' ? <ConfiguratorPage session={session} /> : null}
      {tab === 'orders' ? <MyOrdersPage session={session} /> : null}
      {tab === 'staff' ? <StaffOrdersPage session={session} /> : null}
    </main>
  )
}

export default App
