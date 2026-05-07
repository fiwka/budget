package xyz.fiwka.budget.dataservice.infrastructure.dto.request.budget

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole

data class AddBudgetMemberRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val login: String,

    @field:NotNull
    val role: BudgetRole,
)

data class UpdateBudgetMemberRoleRequest(
    @field:NotNull
    val role: BudgetRole,
)
