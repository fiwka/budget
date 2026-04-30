package xyz.fiwka.budget.dataservice.infrastructure.dto.request.transaction

import tools.jackson.databind.JsonNode
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class TransactionFieldsRequest(
    @field:NotNull
    val categoryId: UUID,
    @field:NotNull
    val completedDate: Instant,
    @field:NotNull
    @field:DecimalMin(value = "0.01", inclusive = true)
    val amount: BigDecimal,
    val appendix: Map<String, Any>? = null,
)

