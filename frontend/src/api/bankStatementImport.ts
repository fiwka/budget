import type { BankStatementImportResponse } from '../types/domain'
import { request } from './client'

export type BankStatementImportFields = {
  budgetId: string
  file: File
  bank?: 'VTB'
  format?: 'PDF'
  zoneId?: string
}

export function importBankStatement(payload: BankStatementImportFields) {
  const form = new FormData()
  form.append('file', payload.file)
  form.append('bank', payload.bank ?? 'VTB')
  form.append('format', payload.format ?? 'PDF')
  if (payload.zoneId) form.append('zoneId', payload.zoneId)

  return request<BankStatementImportResponse>(`/api/bank-statement-import/budget/${payload.budgetId}`, {
    method: 'POST',
    body: form,
  })
}
