package xyz.fiwka.budget.dataservice.application.port.out.access

import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface SaveBudgetRoleForUserOutputPort : Port<SaveBudgetRoleForUserRequest, Unit>

data class SaveBudgetRoleForUserRequest(
    val budgetId: UUID,
    val userId: UUID,
    val role: BudgetRole,
)

