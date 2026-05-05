package xyz.fiwka.budget.mcpserver.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class UserInfoResponse(
    val id: UUID,
    val username: String,
    val email: String,
)

data class PageResponse<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

data class TransactionResponse(
    val id: UUID,
    val categoryId: UUID,
    val completedDate: Instant,
    val amount: BigDecimal,
    val appendix: Map<String, Any>? = null,
)

data class CategoryResponse(
    val id: UUID,
    val budgetId: UUID,
    val name: String,
    val isConsumption: Boolean,
)

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
