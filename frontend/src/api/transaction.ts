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
  amount: number
}

function toMoneyAmount(value: number) {
  return Math.round((value + Number.EPSILON) * 100) / 100
}

export function createTransaction(payload: TransactionFields) {
  const { categoryId, completedDate, amount } = payload
  return request<Transaction>('/api/transaction', {
    method: 'POST',
    body: JSON.stringify({ categoryId, completedDate, amount: toMoneyAmount(amount) }),
  })
}

export function updateTransaction(id: string, payload: TransactionFields) {
  return request<Transaction>(`/api/transaction/${id}`, {
    method: 'PUT',
    body: JSON.stringify({ ...payload, amount: toMoneyAmount(payload.amount) }),
  })
}

export function deleteTransaction(id: string) {
  return request<void>(`/api/transaction/${id}`, { method: 'DELETE' })
}

