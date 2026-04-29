import { DEFAULT_PAGE_SIZE } from '../config'
import type { AccessibleBudget, Budget, Page } from '../types/domain'
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

