package xyz.fiwka.budget.dataservice.application.port.out.budget

import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.domain.budget.AccessibleBudget
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface ListAccessibleBudgetsOutputPort : Port<ListAccessibleBudgetsRequest, PageResult<AccessibleBudget>>

data class ListAccessibleBudgetsRequest(
    val userId: UUID,
    val page: Int,
    val size: Int,
    val budgetId: UUID? = null,
    val name: String? = null,
    val description: String? = null,
    val role: BudgetRole? = null,
)

