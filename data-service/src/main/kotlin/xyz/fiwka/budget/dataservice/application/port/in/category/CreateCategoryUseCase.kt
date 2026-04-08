package xyz.fiwka.budget.dataservice.application.port.`in`.category

import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface CreateCategoryUseCase : Port<CreateCategoryCommand, CreateCategoryResponse>

data class CreateCategoryCommand(
    val budgetId: UUID,
    val name: String,
    val isConsumption: Boolean
)

@JvmInline
value class CreateCategoryResponse(val category: Category)

