import { createBrowserRouter, Navigate, useParams } from 'react-router-dom'
import { AppLayout, GuestLayout, ProtectedLayout } from './App'
import { AuthPage } from './pages/AuthPage'
import { BudgetWorkspacePage } from './pages/BudgetWorkspacePage'
import { BudgetsPage } from './pages/BudgetsPage'

function BudgetIndexRedirect() {
  const { budgetId } = useParams<{ budgetId: string }>()
  if (!budgetId) return <Navigate to="/budgets" replace />
  return <Navigate to={`/budgets/${budgetId}/transactions`} replace />
}

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [
      { index: true, element: <Navigate to="/budgets" replace /> },
      {
        element: <GuestLayout />,
        children: [{ path: '/auth', element: <AuthPage /> }],
      },
      {
        element: <ProtectedLayout />,
        children: [
          { path: '/budgets', element: <BudgetsPage /> },
          { path: '/budgets/:budgetId', element: <BudgetIndexRedirect /> },
          { path: '/budgets/:budgetId/:tab', element: <BudgetWorkspacePage /> },
        ],
      },
    ],
  },
])
