package xyz.fiwka.budget.dataservice.infrastructure.dto.response.category

import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val budgetId: UUID,
    val name: String,
    val isConsumption: Boolean
)

