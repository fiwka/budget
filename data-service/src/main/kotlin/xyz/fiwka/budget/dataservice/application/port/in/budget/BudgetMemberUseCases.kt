package xyz.fiwka.budget.dataservice.application.port.`in`.budget

import xyz.fiwka.budget.dataservice.domain.budget.BudgetMember
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface ListBudgetMembersUseCase : Port<ListBudgetMembersCommand, ListBudgetMembersResponse>

data class ListBudgetMembersCommand(
    val budgetId: UUID,
    val actorLogin: String,
)

@JvmInline
value class ListBudgetMembersResponse(val members: List<BudgetMember>)

interface AddBudgetMemberUseCase : Port<AddBudgetMemberCommand, BudgetMemberResponse>

data class AddBudgetMemberCommand(
    val budgetId: UUID,
    val login: String,
    val role: BudgetRole,
    val actorLogin: String,
)

interface UpdateBudgetMemberRoleUseCase : Port<UpdateBudgetMemberRoleCommand, BudgetMemberResponse>

data class UpdateBudgetMemberRoleCommand(
    val budgetId: UUID,
    val userId: UUID,
    val role: BudgetRole,
    val actorLogin: String,
)

interface RemoveBudgetMemberUseCase : Port<RemoveBudgetMemberCommand, Unit>

data class RemoveBudgetMemberCommand(
    val budgetId: UUID,
    val userId: UUID,
    val actorLogin: String,
)

@JvmInline
value class BudgetMemberResponse(val member: BudgetMember)
