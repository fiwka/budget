package xyz.fiwka.budget.dataservice.application.port.out.access

import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface FindBudgetRoleForUserOutputPort : Port<FindBudgetRoleForUserRequest, BudgetRole?>

data class FindBudgetRoleForUserRequest(
    val userId: UUID,
    val budgetId: UUID,
)

