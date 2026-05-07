package xyz.fiwka.budget.dataservice.domain.budget

import java.util.UUID

data class BudgetMember(
    val userId: UUID,
    val username: String,
    val email: String,
    val role: BudgetRole,
)
