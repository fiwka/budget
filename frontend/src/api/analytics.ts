import type { BudgetAnalyticsSummary } from '../types/domain'
import { request } from './client'

export function getMonthlySummary(budgetId: string, period?: string) {
  const params = new URLSearchParams({ budgetId })
  if (period) params.set('period', period)
  return request<BudgetAnalyticsSummary>(`/api/analytics/budget/monthly-summary?${params.toString()}`)
}
