package xyz.fiwka.budget.dataservice.infrastructure.dto.response.budget

import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import java.util.UUID

data class AccessibleBudgetResponse(
    val id: UUID,
    val name: String,
    val description: String,
    val role: BudgetRole,
)

