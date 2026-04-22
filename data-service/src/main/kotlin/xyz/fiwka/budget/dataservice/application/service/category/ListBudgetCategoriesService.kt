package xyz.fiwka.budget.dataservice.application.service.category

import xyz.fiwka.budget.dataservice.application.port.`in`.category.ListBudgetCategoriesCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ListBudgetCategoriesResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ListBudgetCategoriesUseCase
import xyz.fiwka.budget.dataservice.application.port.out.category.ListBudgetCategoriesOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.ListBudgetCategoriesRequest
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetPermission

class ListBudgetCategoriesService(
    private val budgetAccessGuard: BudgetAccessGuard,
    private val listBudgetCategoriesOutputPort: ListBudgetCategoriesOutputPort,
) : ListBudgetCategoriesUseCase {

    override fun execute(request: ListBudgetCategoriesCommand): ListBudgetCategoriesResponse {
        budgetAccessGuard.requireBudgetPermission(request.actorLogin, request.budgetId, BudgetPermission.VIEW)

        return ListBudgetCategoriesResponse(
            listBudgetCategoriesOutputPort.execute(
                ListBudgetCategoriesRequest(
                    budgetId = request.budgetId,
                    page = request.page,
                    size = request.size,
                    id = request.id,
                    name = request.name,
                    isConsumption = request.isConsumption,
                )
            )
        )
    }
}

