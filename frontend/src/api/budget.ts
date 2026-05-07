import { DEFAULT_PAGE_SIZE } from '../config'
import type { AccessibleBudget, Budget, BudgetMember, BudgetRole, Page } from '../types/domain'
import { request } from './client'

function pageParams(page: number, size = DEFAULT_PAGE_SIZE) {
  return `?page=${page}&size=${size}`
}

export function listBudgets(page = 0, size = DEFAULT_PAGE_SIZE) {
  return request<Page<AccessibleBudget>>(`/api/budget/accessible${pageParams(page, size)}`)
}

export function createBudget(payload: Pick<Budget, 'name' | 'description'>) {
  return request<Budget>('/api/budget', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateBudget(id: string, payload: Pick<Budget, 'name' | 'description'>) {
  return request<Budget>(`/api/budget/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function readBudget(id: string) {
  return request<Budget>(`/api/budget/${id}`)
}

export function deleteBudget(id: string) {
  return request<void>(`/api/budget/${id}`, { method: 'DELETE' })
}

export function listBudgetMembers(budgetId: string) {
  return request<BudgetMember[]>(`/api/budget/${budgetId}/members`)
}

export function addBudgetMember(budgetId: string, payload: { login: string; role: BudgetRole }) {
  return request<BudgetMember>(`/api/budget/${budgetId}/members`, {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateBudgetMemberRole(budgetId: string, userId: string, role: BudgetRole) {
  return request<BudgetMember>(`/api/budget/${budgetId}/members/${userId}`, {
    method: 'PUT',
    body: JSON.stringify({ role }),
  })
}

export function removeBudgetMember(budgetId: string, userId: string) {
  return request<void>(`/api/budget/${budgetId}/members/${userId}`, { method: 'DELETE' })
}

