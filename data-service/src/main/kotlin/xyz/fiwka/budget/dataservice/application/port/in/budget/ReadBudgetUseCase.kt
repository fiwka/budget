package xyz.fiwka.budget.dataservice.application.port.`in`.budget

import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface ReadBudgetUseCase : Port<ReadBudgetCommand, ReadBudgetResponse>

data class ReadBudgetCommand(
    val id: UUID,
    val actorLogin: String,
)

@JvmInline
value class ReadBudgetResponse(val budget: Budget)

