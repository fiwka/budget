package xyz.fiwka.budget.dataservice.application.port.`in`.category

import xyz.fiwka.budget.port.Port
import java.util.UUID

interface DeleteCategoryUseCase : Port<DeleteCategoryCommand, Unit>

data class DeleteCategoryCommand(
    val id: UUID,
    val actorLogin: String,
)

