package xyz.fiwka.budget.dataservice.infrastructure.dto.response.transaction

import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class TransactionResponse(
    val id: UUID,
    val categoryId: UUID,
    val completedDate: Instant,
    val amount: BigDecimal,
    val appendix: Map<String, Any>? = null,
)

