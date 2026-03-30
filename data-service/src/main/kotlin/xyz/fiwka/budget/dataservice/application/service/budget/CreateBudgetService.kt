package xyz.fiwka.budget.dataservice.application.service.budget

import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.out.budget.SaveBudgetOutputPort
import xyz.fiwka.budget.dataservice.domain.budget.Budget

class CreateBudgetService(
    private val saveBudgetOutputPort: SaveBudgetOutputPort
) : CreateBudgetUseCase {
    override fun execute(request: CreateBudgetCommand): CreateBudgetResponse =
        CreateBudgetResponse(
            saveBudgetOutputPort.execute(Budget(null, request.name, request.description))
        )
}