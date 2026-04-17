package xyz.fiwka.budget.dataservice.application.service.transaction

import xyz.fiwka.budget.dataservice.application.exception.category.CategoryNotFoundException
import xyz.fiwka.budget.dataservice.application.exception.transaction.TransactionNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetPermission

class ReadTransactionService(
    private val findTransactionByIdOutputPort: FindTransactionByIdOutputPort,
    private val findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
    private val budgetAccessGuard: BudgetAccessGuard,
) : ReadTransactionUseCase {
    override fun execute(request: ReadTransactionCommand): ReadTransactionResponse {
        val transaction = findTransactionByIdOutputPort.execute(request.id)
            ?: throw TransactionNotFoundException(request.id)

        val category = findCategoryByIdOutputPort.execute(transaction.categoryId)
            ?: throw CategoryNotFoundException(transaction.categoryId)

        budgetAccessGuard.requireBudgetPermission(request.actorLogin, category.budgetId, BudgetPermission.VIEW)

        return ReadTransactionResponse(transaction)
    }
}

