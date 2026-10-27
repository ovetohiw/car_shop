import { NavLink, Outlet } from 'react-router-dom'
import { useState } from 'react'
import { useAuth } from './AuthContext'

const NAV = [
  { to: '/', label: 'Cars', end: true },
  { to: '/parts', label: 'Parts' },
  { to: '/configure', label: 'Configurator' },
  { to: '/orders', label: 'My orders' },
  { to: '/staff', label: 'Staff' },
] as const

export function AppShell() {
  const { session, authError, signIn, signUp, signOut } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  return (
    <div className="app-shell">
      <header className="site-header">
        <div className="site-header__inner">
          <p className="brand">Car Shop</p>
          <nav className="site-nav" aria-label="Main">
            {NAV.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={'end' in item ? item.end : false}
                className={({ isActive }) =>
                  isActive ? 'site-nav__link is-active' : 'site-nav__link'
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>

          <section className="auth-bar" aria-label="Account">
            {session ? (
              <div className="auth-bar__session">
                <p>
                  <span className="auth-bar__email">{session.email}</span>
                  <span className="auth-bar__role">{session.role}</span>
                </p>
                <button type="button" className="btn btn--ghost" onClick={signOut}>
                  Sign out
                </button>
              </div>
            ) : (
              <form
                className="auth-bar__form"
                onSubmit={(event) => {
                  event.preventDefault()
                  void signIn(email, password)
                }}
              >
                <label className="sr-only" htmlFor="auth-email">
                  Email
                </label>
                <input
                  id="auth-email"
                  className="field"
                  placeholder="email"
                  autoComplete="username"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                />
                <label className="sr-only" htmlFor="auth-password">
                  Password
                </label>
                <input
                  id="auth-password"
                  className="field"
                  placeholder="password"
                  type="password"
                  autoComplete="current-password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                />
                <button type="submit" className="btn btn--solid">
                  Sign in
                </button>
                <button
                  type="button"
                  className="btn btn--ghost"
                  onClick={() => void signUp(email, password)}
                >
                  Register
                </button>
              </form>
            )}
            {authError ? <p className="form-error">{authError}</p> : null}
          </section>
        </div>
      </header>

      <main className="page">
        <Outlet />
      </main>
    </div>
  )
}
