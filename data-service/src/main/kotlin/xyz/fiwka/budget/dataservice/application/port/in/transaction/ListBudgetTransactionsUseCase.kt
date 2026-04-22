package xyz.fiwka.budget.dataservice.application.port.`in`.transaction

import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.port.Port
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

interface ListBudgetTransactionsUseCase : Port<ListBudgetTransactionsCommand, ListBudgetTransactionsResponse>

data class ListBudgetTransactionsCommand(
    val budgetId: UUID,
    val actorLogin: String,
    val page: Int,
    val size: Int,
    val id: UUID? = null,
    val categoryId: UUID? = null,
    val completedDateFrom: Instant? = null,
    val completedDateTo: Instant? = null,
    val amountFrom: BigDecimal? = null,
    val amountTo: BigDecimal? = null,
    val appendixContains: String? = null,
)

@JvmInline
value class ListBudgetTransactionsResponse(val transactions: PageResult<Transaction>)

