package xyz.fiwka.budget.dataservice.application.service.budget

import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.budget.BudgetNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.DeleteBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.DeleteBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.out.budget.DeleteBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort

class DeleteBudgetService(
    private val findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
    private val deleteBudgetByIdOutputPort: DeleteBudgetByIdOutputPort,
    private val atomicOperationExecutor: AtomicOperationExecutor
) : DeleteBudgetUseCase {
    override fun execute(request: DeleteBudgetCommand) {
        atomicOperationExecutor.execute {
            findBudgetByIdOutputPort.execute(request.id)
                ?: throw BudgetNotFoundException(request.id)

            deleteBudgetByIdOutputPort.execute(request.id)
        }
    }
}
