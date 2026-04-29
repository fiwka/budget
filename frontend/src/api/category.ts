import { DEFAULT_PAGE_SIZE } from '../config'
import type { Category, Page } from '../types/domain'
import { request } from './client'

function pageParams(page: number, size = DEFAULT_PAGE_SIZE) {
  return `?page=${page}&size=${size}`
}

export function listCategories(budgetId: string, page = 0, size = DEFAULT_PAGE_SIZE) {
  return request<Page<Category>>(`/api/category/budget/${budgetId}${pageParams(page, size)}`)
}

export function createCategory(payload: Pick<Category, 'budgetId' | 'name' | 'isConsumption'>) {
  return request<Category>('/api/category', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateCategory(id: string, payload: Pick<Category, 'budgetId' | 'name' | 'isConsumption'>) {
  return request<Category>(`/api/category/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function deleteCategory(id: string) {
  return request<void>(`/api/category/${id}`, { method: 'DELETE' })
}

