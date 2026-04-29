import { DEFAULT_PAGE_SIZE } from '../config'
import type { Page, Transaction } from '../types/domain'
import { request } from './client'

function pageParams(page: number, size = DEFAULT_PAGE_SIZE) {
  return `?page=${page}&size=${size}`
}

export function listTransactions(budgetId: string, page = 0, size = DEFAULT_PAGE_SIZE) {
  return request<Page<Transaction>>(`/api/transaction/budget/${budgetId}${pageParams(page, size)}`)
}

export type TransactionFields = {
  categoryId: string
  completedDate: string
  amount: string
  appendix?: unknown
}

export function createTransaction(payload: TransactionFields) {
  return request<Transaction>('/api/transaction', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateTransaction(id: string, payload: TransactionFields) {
  return request<Transaction>(`/api/transaction/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function deleteTransaction(id: string) {
  return request<void>(`/api/transaction/${id}`, { method: 'DELETE' })
}

