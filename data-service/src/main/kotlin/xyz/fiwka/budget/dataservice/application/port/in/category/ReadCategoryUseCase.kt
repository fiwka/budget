package xyz.fiwka.budget.dataservice.application.port.`in`.category

import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface ReadCategoryUseCase : Port<ReadCategoryCommand, ReadCategoryResponse>

data class ReadCategoryCommand(
    val id: UUID,
    val actorLogin: String,
)

@JvmInline
value class ReadCategoryResponse(val category: Category)

