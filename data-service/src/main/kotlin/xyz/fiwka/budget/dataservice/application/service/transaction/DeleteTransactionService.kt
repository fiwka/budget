package xyz.fiwka.budget.dataservice.application.service.transaction

import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.category.CategoryNotFoundException
import xyz.fiwka.budget.dataservice.application.exception.transaction.TransactionNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.DeleteTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.DeleteTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.DeleteTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetPermission

class DeleteTransactionService(
    private val findTransactionByIdOutputPort: FindTransactionByIdOutputPort,
    private val findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
    private val deleteTransactionByIdOutputPort: DeleteTransactionByIdOutputPort,
    private val budgetAccessGuard: BudgetAccessGuard,
    private val atomicOperationExecutor: AtomicOperationExecutor,
) : DeleteTransactionUseCase {
    override fun execute(request: DeleteTransactionCommand) {
        atomicOperationExecutor.execute {
            val transaction = findTransactionByIdOutputPort.execute(request.id)
                ?: throw TransactionNotFoundException(request.id)

            val category = findCategoryByIdOutputPort.execute(transaction.categoryId)
                ?: throw CategoryNotFoundException(transaction.categoryId)

            budgetAccessGuard.requireBudgetPermission(request.actorLogin, category.budgetId, BudgetPermission.EDIT)

            deleteTransactionByIdOutputPort.execute(request.id)
        }
    }
}

