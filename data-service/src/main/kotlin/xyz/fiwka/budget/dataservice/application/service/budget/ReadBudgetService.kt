package xyz.fiwka.budget.dataservice.application.service.budget

import xyz.fiwka.budget.dataservice.application.exception.budget.BudgetNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort

class ReadBudgetService(
    private val findBudgetByIdOutputPort: FindBudgetByIdOutputPort
) : ReadBudgetUseCase {
    override fun execute(request: ReadBudgetCommand): ReadBudgetResponse =
        ReadBudgetResponse(
            findBudgetByIdOutputPort.execute(request.id)
                ?: throw BudgetNotFoundException(request.id)
        )
}

