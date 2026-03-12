package xyz.fiwka.budget.analytics.model.event

import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.UUID

data class TransactionCreatedEvent(
    val transactionId: UUID,
    val categoryId: UUID,
    val completedDate: ZonedDateTime,
    val amount: BigDecimal,
    val appendix: String?
)
