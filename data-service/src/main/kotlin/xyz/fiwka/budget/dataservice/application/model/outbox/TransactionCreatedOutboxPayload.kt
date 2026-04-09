package xyz.fiwka.budget.dataservice.application.model.outbox

import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import java.util.UUID

data class TransactionCreatedOutboxPayload(
    val transactionId: UUID,
    val categoryId: UUID,
    val completedDate: Long,
    val amount: String,
    val appendix: String,
) {
    companion object {
        fun fromTransaction(transaction: Transaction): TransactionCreatedOutboxPayload =
            TransactionCreatedOutboxPayload(
                transactionId = requireNotNull(transaction.id),
                categoryId = transaction.categoryId,
                completedDate = transaction.completedDate.toEpochMilli(),
                amount = transaction.amount.toPlainString(),
                appendix = transaction.appendix?.toString() ?: "null",
            )
    }
}

