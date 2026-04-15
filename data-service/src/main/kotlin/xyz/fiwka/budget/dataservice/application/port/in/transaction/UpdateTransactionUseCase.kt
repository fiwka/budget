package xyz.fiwka.budget.dataservice.application.port.`in`.transaction

import tools.jackson.databind.JsonNode
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.port.Port
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

interface UpdateTransactionUseCase : Port<UpdateTransactionCommand, UpdateTransactionResponse>

data class UpdateTransactionCommand(
    val id: UUID,
    val categoryId: UUID,
    val completedDate: Instant,
    val amount: BigDecimal,
    val appendix: JsonNode? = null,
)

@JvmInline
value class UpdateTransactionResponse(val transaction: Transaction)

