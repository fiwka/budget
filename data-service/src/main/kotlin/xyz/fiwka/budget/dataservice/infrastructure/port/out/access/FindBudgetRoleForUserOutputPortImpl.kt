package xyz.fiwka.budget.dataservice.infrastructure.port.out.access

import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserRequest
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.infrastructure.repository.BudgetRoleRepository

class FindBudgetRoleForUserOutputPortImpl(
    private val budgetRoleRepository: BudgetRoleRepository,
) : FindBudgetRoleForUserOutputPort {

    override fun execute(request: FindBudgetRoleForUserRequest): BudgetRole? =
        budgetRoleRepository.findRoleKeyByUserIdAndBudgetId(request.userId, request.budgetId)
            ?.let(BudgetRole::fromKey)
}


