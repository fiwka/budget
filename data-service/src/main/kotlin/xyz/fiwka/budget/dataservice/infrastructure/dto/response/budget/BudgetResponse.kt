package xyz.fiwka.budget.dataservice.infrastructure.dto.response.budget

import java.util.UUID

data class BudgetResponse(
    val id: UUID,
    val name: String,
    val description: String
)