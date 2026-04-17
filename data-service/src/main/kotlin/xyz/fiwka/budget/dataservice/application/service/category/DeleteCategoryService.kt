package xyz.fiwka.budget.dataservice.application.service.category

import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.category.CategoryNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.category.DeleteCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.DeleteCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.out.category.DeleteCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetPermission

class DeleteCategoryService(
    private val findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
    private val deleteCategoryByIdOutputPort: DeleteCategoryByIdOutputPort,
    private val budgetAccessGuard: BudgetAccessGuard,
    private val atomicOperationExecutor: AtomicOperationExecutor
) : DeleteCategoryUseCase {
    override fun execute(request: DeleteCategoryCommand) {
        atomicOperationExecutor.execute {
            val category = findCategoryByIdOutputPort.execute(request.id)
                ?: throw CategoryNotFoundException(request.id)

            budgetAccessGuard.requireBudgetPermission(request.actorLogin, category.budgetId, BudgetPermission.EDIT)

            deleteCategoryByIdOutputPort.execute(request.id)
        }
    }
}
