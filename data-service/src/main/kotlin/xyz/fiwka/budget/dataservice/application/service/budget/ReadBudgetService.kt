package xyz.fiwka.budget.dataservice.application.service.budget

import xyz.fiwka.budget.dataservice.application.exception.budget.BudgetNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetPermission

class ReadBudgetService(
    private val findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
    private val budgetAccessGuard: BudgetAccessGuard,
) : ReadBudgetUseCase {
    override fun execute(request: ReadBudgetCommand): ReadBudgetResponse {
        budgetAccessGuard.requireBudgetPermission(request.actorLogin, request.id, BudgetPermission.VIEW)

        return ReadBudgetResponse(
            findBudgetByIdOutputPort.execute(request.id)
                ?: throw BudgetNotFoundException(request.id)
        )
    }
}

