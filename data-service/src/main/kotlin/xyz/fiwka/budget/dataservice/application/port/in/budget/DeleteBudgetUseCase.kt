package xyz.fiwka.budget.dataservice.application.port.`in`.budget

import xyz.fiwka.budget.port.Port
import java.util.UUID

interface DeleteBudgetUseCase : Port<DeleteBudgetCommand, Unit>

data class DeleteBudgetCommand(
    val id: UUID
)

