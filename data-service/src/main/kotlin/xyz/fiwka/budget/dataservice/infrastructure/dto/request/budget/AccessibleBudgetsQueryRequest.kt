package xyz.fiwka.budget.dataservice.infrastructure.dto.request.budget

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import java.util.UUID

data class AccessibleBudgetsQueryRequest(
    val budgetId: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val role: BudgetRole? = null,
    @field:Min(0)
    val page: Int = 0,
    @field:Min(1)
    @field:Max(100)
    val size: Int = 20,
)

