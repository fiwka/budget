package xyz.fiwka.budget.dataservice.infrastructure.dto.request.category

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class CategoryFieldsRequest(
    @field:NotNull
    val budgetId: UUID,
    @field:NotBlank
    @field:Size(max = 255, min = 2)
    val name: String,
    @get:JsonProperty("isConsumption")
    @param:JsonProperty("isConsumption")
    val isConsumption: Boolean
)

