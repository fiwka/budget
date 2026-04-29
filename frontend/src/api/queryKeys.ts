export const queryKeys = {
  session: ['session'] as const,
  user: ['user'] as const,
  budgets: (page: number) => ['budgets', page] as const,
  budget: (id: string) => ['budget', id] as const,
  categories: (budgetId: string, page: number) => ['categories', budgetId, page] as const,
  transactions: (budgetId: string, page: number) => ['transactions', budgetId, page] as const,
}
