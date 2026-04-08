package xyz.fiwka.budget.dataservice.application.service.category

import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.budget.BudgetNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.category.CreateCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.CreateCategoryResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.category.CreateCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.SaveCategoryOutputPort

class CreateCategoryService(
    private val findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
    private val saveCategoryOutputPort: SaveCategoryOutputPort,
    private val atomicOperationExecutor: AtomicOperationExecutor
) : CreateCategoryUseCase {
    override fun execute(request: CreateCategoryCommand): CreateCategoryResponse =
        atomicOperationExecutor.execute {
            val budget = findBudgetByIdOutputPort.execute(request.budgetId)
                ?: throw BudgetNotFoundException(request.budgetId)

            CreateCategoryResponse(
                saveCategoryOutputPort.execute(
                    budget.createCategory(request.name, request.isConsumption)
                )
            )
        }
}
