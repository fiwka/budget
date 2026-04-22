package xyz.fiwka.budget.dataservice.application.service.transaction

import xyz.fiwka.budget.dataservice.application.exception.type.BadRequestException
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ListBudgetTransactionsCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ListBudgetTransactionsResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ListBudgetTransactionsUseCase
import xyz.fiwka.budget.dataservice.application.port.out.transaction.ListBudgetTransactionsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.ListBudgetTransactionsRequest
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetPermission

class ListBudgetTransactionsService(
    private val budgetAccessGuard: BudgetAccessGuard,
    private val listBudgetTransactionsOutputPort: ListBudgetTransactionsOutputPort,
) : ListBudgetTransactionsUseCase {

    override fun execute(request: ListBudgetTransactionsCommand): ListBudgetTransactionsResponse {
        if (request.completedDateFrom != null && request.completedDateTo != null && request.completedDateFrom > request.completedDateTo) {
            throw BadRequestException("completedDateFrom must be less than or equal to completedDateTo")
        }
        if (request.amountFrom != null && request.amountTo != null && request.amountFrom > request.amountTo) {
            throw BadRequestException("amountFrom must be less than or equal to amountTo")
        }

        budgetAccessGuard.requireBudgetPermission(request.actorLogin, request.budgetId, BudgetPermission.VIEW)

        return ListBudgetTransactionsResponse(
            listBudgetTransactionsOutputPort.execute(
                ListBudgetTransactionsRequest(
                    budgetId = request.budgetId,
                    page = request.page,
                    size = request.size,
                    id = request.id,
                    categoryId = request.categoryId,
                    completedDateFrom = request.completedDateFrom,
                    completedDateTo = request.completedDateTo,
                    amountFrom = request.amountFrom,
                    amountTo = request.amountTo,
                    appendixContains = request.appendixContains,
                )
            )
        )
    }
}

