package xyz.fiwka.budget.dataservice.application.port.`in`.budget

import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.port.Port

interface CreateBudgetUseCase : Port<CreateBudgetCommand, CreateBudgetResponse>

data class CreateBudgetCommand(
    val name: String,
    val description: String
)

@JvmInline
value class CreateBudgetResponse(val budget: Budget)