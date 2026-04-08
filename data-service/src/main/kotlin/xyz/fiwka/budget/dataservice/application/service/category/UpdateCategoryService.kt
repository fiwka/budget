package xyz.fiwka.budget.dataservice.application.service.category

import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.budget.BudgetNotFoundException
import xyz.fiwka.budget.dataservice.application.exception.category.CategoryBelongsToAnotherBudgetException
import xyz.fiwka.budget.dataservice.application.exception.category.CategoryNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.category.UpdateCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.UpdateCategoryResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.category.UpdateCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.UpdateCategoryOutputPort

class UpdateCategoryService(
    private val findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
    private val findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
    private val updateCategoryOutputPort: UpdateCategoryOutputPort,
    private val atomicOperationExecutor: AtomicOperationExecutor
) : UpdateCategoryUseCase {
    override fun execute(request: UpdateCategoryCommand): UpdateCategoryResponse =
        atomicOperationExecutor.execute {
            val category = findCategoryByIdOutputPort.execute(request.id)
                ?: throw CategoryNotFoundException(request.id)

            val budget = findBudgetByIdOutputPort.execute(request.budgetId)
                ?: throw BudgetNotFoundException(request.budgetId)

            if (!budget.owns(category)) {
                throw CategoryBelongsToAnotherBudgetException(request.id, request.budgetId)
            }

            UpdateCategoryResponse(
                updateCategoryOutputPort.execute(
                    budget.updateCategory(category, request.name, request.isConsumption)
                )
            )
        }
}
