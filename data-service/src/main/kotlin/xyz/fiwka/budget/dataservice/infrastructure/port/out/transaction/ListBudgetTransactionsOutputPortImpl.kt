package xyz.fiwka.budget.dataservice.infrastructure.port.out.transaction

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.application.port.out.transaction.ListBudgetTransactionsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.ListBudgetTransactionsRequest
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.dataservice.infrastructure.mapper.TransactionMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.TransactionRepository

@Component
class ListBudgetTransactionsOutputPortImpl(
    private val transactionRepository: TransactionRepository,
    private val transactionMapper: TransactionMapper,
) : ListBudgetTransactionsOutputPort {

    override fun execute(request: ListBudgetTransactionsRequest): PageResult<Transaction> {
        val page = transactionRepository.findBudgetTransactions(
            budgetId = request.budgetId,
            id = request.id,
            categoryId = request.categoryId,
            completedDateFrom = request.completedDateFrom,
            completedDateTo = request.completedDateTo,
            amountFrom = request.amountFrom,
            amountTo = request.amountTo,
            appendixContains = request.appendixContains,
            pageable = PageRequest.of(request.page, request.size),
        )

        return PageResult(
            items = page.content.map(transactionMapper::fromEntity),
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
        )
    }
}

