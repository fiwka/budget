package xyz.fiwka.budget.dataservice.infrastructure.dto.response.budget

import xyz.fiwka.budget.dataservice.domain.budget.BudgetMember
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import java.util.UUID

data class BudgetMemberResponse(
    val userId: UUID,
    val username: String,
    val email: String,
    val role: BudgetRole,
)

fun BudgetMember.toResponse(): BudgetMemberResponse =
    BudgetMemberResponse(
        userId = userId,
        username = username,
        email = email,
        role = role,
    )
