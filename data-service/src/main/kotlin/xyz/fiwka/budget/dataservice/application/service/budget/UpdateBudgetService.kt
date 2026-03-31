package xyz.fiwka.budget.dataservice.application.service.budget

import xyz.fiwka.budget.dataservice.application.exception.budget.BudgetNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.UpdateBudgetOutputPort
import xyz.fiwka.budget.dataservice.domain.budget.Budget

class UpdateBudgetService(
    private val findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
    private val updateBudgetOutputPort: UpdateBudgetOutputPort
) : UpdateBudgetUseCase {
    override fun execute(request: UpdateBudgetCommand): UpdateBudgetResponse {
        findBudgetByIdOutputPort.execute(request.id)
            ?: throw BudgetNotFoundException(request.id)

        return UpdateBudgetResponse(
            updateBudgetOutputPort.execute(
                Budget(request.id, request.name, request.description)
            )
        )
    }
}

