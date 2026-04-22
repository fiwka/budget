package xyz.fiwka.budget.dataservice.application.port.out.transaction

import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.port.Port
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

interface ListBudgetTransactionsOutputPort : Port<ListBudgetTransactionsRequest, PageResult<Transaction>>

data class ListBudgetTransactionsRequest(
    val budgetId: UUID,
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

