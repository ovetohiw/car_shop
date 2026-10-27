import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './AuthContext'
import { AppShell } from './AppShell'
import { CatalogPage } from './CatalogPage'
import { ConfiguratorPage } from './ConfiguratorPage'
import { MyOrdersPage } from './MyOrdersPage'
import { PartsPage } from './PartsPage'
import { StaffOrdersPage } from './StaffOrdersPage'

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route element={<AppShell />}>
            <Route index element={<CatalogPage />} />
            <Route path="parts" element={<PartsPage />} />
            <Route path="configure" element={<ConfiguratorPage />} />
            <Route path="orders" element={<MyOrdersPage />} />
            <Route path="staff" element={<StaffOrdersPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App
