package xyz.fiwka.budget.dataservice.infrastructure.dto.request.category

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.util.UUID

data class CategoryListQueryRequest(
    val id: UUID? = null,
    val name: String? = null,
    val isConsumption: Boolean? = null,
    @field:Min(0)
    val page: Int = 0,
    @field:Min(1)
    @field:Max(100)
    val size: Int = 20,
)

