package xyz.fiwka.budget.dataservice.application.port.`in`.transaction

import tools.jackson.databind.JsonNode
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.port.Port
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

interface CreateTransactionUseCase : Port<CreateTransactionCommand, CreateTransactionResponse>

data class CreateTransactionCommand(
    val categoryId: UUID,
    val completedDate: Instant,
    val amount: BigDecimal,
    val actorLogin: String,
    val appendix: Map<String, Any>? = null,
)

@JvmInline
value class CreateTransactionResponse(val transaction: Transaction)

