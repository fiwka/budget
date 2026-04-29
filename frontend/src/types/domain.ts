export type Page<T> = {
  items: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export type SessionStatus = {
  authenticated: boolean
  tokenType?: string | null
}

export type UserInfo = {
  id: string
  username: string
  email: string
}

export type BudgetRole = 'OWNER' | 'EDITOR' | 'VIEWER' | string

export type Budget = {
  id: string
  name: string
  description: string
}

export type AccessibleBudget = Budget & {
  role: BudgetRole
}

export type Category = {
  id: string
  budgetId: string
  name: string
  isConsumption: boolean
}

export type Transaction = {
  id: string
  categoryId: string
  completedDate: string
  amount: string
  appendix?: unknown
}

export type ApiProblem = {
  status?: number
  title?: string
  detail?: string
  errors?: Record<string, string[]>
}

