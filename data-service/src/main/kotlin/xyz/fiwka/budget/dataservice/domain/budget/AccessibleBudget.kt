package xyz.fiwka.budget.dataservice.domain.budget

data class AccessibleBudget(
    val budget: Budget,
    val role: BudgetRole,
)

