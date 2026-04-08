package xyz.fiwka.budget.dataservice.application.service.category

import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.category.CategoryNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.category.DeleteCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.DeleteCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.out.category.DeleteCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort

class DeleteCategoryService(
    private val findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
    private val deleteCategoryByIdOutputPort: DeleteCategoryByIdOutputPort,
    private val atomicOperationExecutor: AtomicOperationExecutor
) : DeleteCategoryUseCase {
    override fun execute(request: DeleteCategoryCommand) {
        atomicOperationExecutor.execute {
            findCategoryByIdOutputPort.execute(request.id)
                ?: throw CategoryNotFoundException(request.id)

            deleteCategoryByIdOutputPort.execute(request.id)
        }
    }
}
