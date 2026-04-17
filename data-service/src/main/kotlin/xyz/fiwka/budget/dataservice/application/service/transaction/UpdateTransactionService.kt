package xyz.fiwka.budget.dataservice.application.service.transaction

import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.category.CategoryNotFoundException
import xyz.fiwka.budget.dataservice.application.exception.transaction.TransactionNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.UpdateTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.UpdateTransactionResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.UpdateTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.UpdateTransactionOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetPermission

class UpdateTransactionService(
    private val findTransactionByIdOutputPort: FindTransactionByIdOutputPort,
    private val findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
    private val updateTransactionOutputPort: UpdateTransactionOutputPort,
    private val budgetAccessGuard: BudgetAccessGuard,
    private val atomicOperationExecutor: AtomicOperationExecutor,
) : UpdateTransactionUseCase {
    override fun execute(request: UpdateTransactionCommand): UpdateTransactionResponse =
        atomicOperationExecutor.execute {
            val transaction = findTransactionByIdOutputPort.execute(request.id)
                ?: throw TransactionNotFoundException(request.id)

            val currentCategory = findCategoryByIdOutputPort.execute(transaction.categoryId)
                ?: throw CategoryNotFoundException(transaction.categoryId)

            val targetCategory = findCategoryByIdOutputPort.execute(request.categoryId)
                ?: throw CategoryNotFoundException(request.categoryId)

            budgetAccessGuard.requireBudgetPermission(request.actorLogin, currentCategory.budgetId, BudgetPermission.EDIT)

            if (targetCategory.budgetId != currentCategory.budgetId) {
                budgetAccessGuard.requireBudgetPermission(request.actorLogin, targetCategory.budgetId, BudgetPermission.EDIT)
            }

            transaction.update(
                categoryId = request.categoryId,
                completedDate = request.completedDate,
                amount = request.amount,
                appendix = request.appendix,
            )

            UpdateTransactionResponse(updateTransactionOutputPort.execute(transaction))
        }
}

