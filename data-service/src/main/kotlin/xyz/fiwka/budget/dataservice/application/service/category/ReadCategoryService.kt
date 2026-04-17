package xyz.fiwka.budget.dataservice.application.service.category

import xyz.fiwka.budget.dataservice.application.exception.category.CategoryNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetPermission

class ReadCategoryService(
    private val findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
    private val budgetAccessGuard: BudgetAccessGuard,
) : ReadCategoryUseCase {
    override fun execute(request: ReadCategoryCommand): ReadCategoryResponse {
        val category = findCategoryByIdOutputPort.execute(request.id)
            ?: throw CategoryNotFoundException(request.id)

        budgetAccessGuard.requireBudgetPermission(request.actorLogin, category.budgetId, BudgetPermission.VIEW)

        return ReadCategoryResponse(category)
    }
}

