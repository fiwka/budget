package xyz.fiwka.budget.dataservice.application.port.out.access

import xyz.fiwka.budget.dataservice.domain.budget.BudgetMember
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface FindBudgetMemberOutputPort : Port<FindBudgetMemberRequest, BudgetMember?>

data class FindBudgetMemberRequest(
    val budgetId: UUID,
    val userId: UUID,
)

interface UpsertBudgetMemberRoleOutputPort : Port<UpsertBudgetMemberRoleRequest, BudgetMember>

data class UpsertBudgetMemberRoleRequest(
    val budgetId: UUID,
    val userId: UUID,
    val role: BudgetRole,
)

interface DeleteBudgetMemberOutputPort : Port<DeleteBudgetMemberRequest, Unit>

data class DeleteBudgetMemberRequest(
    val budgetId: UUID,
    val userId: UUID,
)
