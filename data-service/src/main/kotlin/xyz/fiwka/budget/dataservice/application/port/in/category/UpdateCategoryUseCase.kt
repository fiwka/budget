package xyz.fiwka.budget.dataservice.application.port.`in`.category

import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface UpdateCategoryUseCase : Port<UpdateCategoryCommand, UpdateCategoryResponse>

data class UpdateCategoryCommand(
    val id: UUID,
    val budgetId: UUID,
    val name: String,
    val isConsumption: Boolean,
    val actorLogin: String,
)

@JvmInline
value class UpdateCategoryResponse(val category: Category)

