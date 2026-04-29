import { Navigate, Outlet } from 'react-router-dom'
import { AuthProvider, useAuth } from './state/auth'
import { ThemeProvider } from './state/theme'

function AuthGate({ requireAuth }: { requireAuth: boolean }) {
  const { initialized, isAuthenticated } = useAuth()

  if (!initialized) return <div className="screen center">Проверка сессии...</div>

  if (requireAuth && !isAuthenticated) return <Navigate to="/auth" replace />
  if (!requireAuth && isAuthenticated) return <Navigate to="/budgets" replace />

  return <Outlet />
}

export function AppLayout() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <Outlet />
      </AuthProvider>
    </ThemeProvider>
  )
}

export function ProtectedLayout() {
  return <AuthGate requireAuth />
}

export function GuestLayout() {
  return <AuthGate requireAuth={false} />
}
