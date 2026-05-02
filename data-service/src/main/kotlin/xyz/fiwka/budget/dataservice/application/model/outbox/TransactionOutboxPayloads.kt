package xyz.fiwka.budget.dataservice.application.model.outbox

import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import java.util.UUID

data class TransactionEventOutboxPayload(
    val transactionId: UUID,
    val categoryId: UUID,
    val budgetId: UUID,
    val isConsumption: Boolean,
    val completedDate: Long,
    val amount: String,
    val appendix: String,
) {
    companion object {
        fun fromTransaction(
            transaction: Transaction,
            budgetId: UUID,
            isConsumption: Boolean,
        ): TransactionEventOutboxPayload =
            TransactionEventOutboxPayload(
                transactionId = requireNotNull(transaction.id),
                categoryId = transaction.categoryId,
                budgetId = budgetId,
                isConsumption = isConsumption,
                completedDate = transaction.completedDate.toEpochMilli(),
                amount = transaction.amount.toPlainString(),
                appendix = transaction.appendix?.toString() ?: "null",
            )
    }
}

data class TransactionDeletedOutboxPayload(
    val transactionId: UUID,
)
