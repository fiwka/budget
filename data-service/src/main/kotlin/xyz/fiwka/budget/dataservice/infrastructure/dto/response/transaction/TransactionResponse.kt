package xyz.fiwka.budget.dataservice.infrastructure.dto.response.transaction

import com.fasterxml.jackson.databind.JsonNode
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class TransactionResponse(
    val id: UUID,
    val categoryId: UUID,
    val completedDate: Instant,
    val amount: BigDecimal,
    val appendix: JsonNode? = null,
)

