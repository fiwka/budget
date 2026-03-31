package xyz.fiwka.budget.dataservice.application.port.`in`.budget

import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface UpdateBudgetUseCase : Port<UpdateBudgetCommand, UpdateBudgetResponse>

data class UpdateBudgetCommand(
    val id: UUID,
    val name: String,
    val description: String
)

@JvmInline
value class UpdateBudgetResponse(val budget: Budget)

