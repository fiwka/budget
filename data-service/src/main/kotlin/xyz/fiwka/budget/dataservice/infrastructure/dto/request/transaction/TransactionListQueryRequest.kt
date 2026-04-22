package xyz.fiwka.budget.dataservice.infrastructure.dto.request.transaction

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class TransactionListQueryRequest(
    val id: UUID? = null,
    val categoryId: UUID? = null,
    val completedDateFrom: Instant? = null,
    val completedDateTo: Instant? = null,
    val amountFrom: BigDecimal? = null,
    val amountTo: BigDecimal? = null,
    val appendixContains: String? = null,
    @field:Min(0)
    val page: Int = 0,
    @field:Min(1)
    @field:Max(100)
    val size: Int = 20,
)

