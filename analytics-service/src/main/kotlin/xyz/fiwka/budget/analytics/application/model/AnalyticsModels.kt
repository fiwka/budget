package xyz.fiwka.budget.analytics.application.model

import java.math.BigDecimal
import java.util.UUID

data class BudgetAnalyticsSummary(
    val budgetId: UUID,
    val period: String,
    val income: BigDecimal,
    val expenses: BigDecimal,
    val balance: BigDecimal,
    val savingsRate: BigDecimal,
    val topExpenseCategories: List<CategoryExpenseShare>,
)

data class CategoryExpenseShare(
    val categoryId: UUID,
    val total: BigDecimal,
    val sharePercent: BigDecimal,
)
