package xyz.fiwka.budget.dataservice.infrastructure.dto.response.category

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val budgetId: UUID,
    val name: String,
    @get:JsonProperty("isConsumption")
    @param:JsonProperty("isConsumption")
    val isConsumption: Boolean
)

