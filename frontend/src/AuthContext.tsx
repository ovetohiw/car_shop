import {
  createContext,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { login, register } from './api'
import type { AuthSession } from './authTypes'

type AuthContextValue = {
  session: AuthSession | null
  authError: string | null
  signIn: (email: string, password: string) => Promise<void>
  signUp: (email: string, password: string) => Promise<void>
  signOut: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(null)
  const [authError, setAuthError] = useState<string | null>(null)

  const value = useMemo<AuthContextValue>(
    () => ({
      session,
      authError,
      async signIn(email, password) {
        try {
          const result = await login(email, password)
          setSession({
            token: result.accessToken,
            email: result.email,
            role: result.role,
          })
          setAuthError(null)
        } catch (err: unknown) {
          setAuthError(err instanceof Error ? err.message : 'Sign in failed')
        }
      },
      async signUp(email, password) {
        try {
          const result = await register(email, password)
          setSession({
            token: result.accessToken,
            email: result.email,
            role: result.role,
          })
          setAuthError(null)
        } catch (err: unknown) {
          setAuthError(err instanceof Error ? err.message : 'Register failed')
        }
      },
      signOut() {
        setSession(null)
        setAuthError(null)
      },
    }),
    [session, authError],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return ctx
}
