package xyz.fiwka.budget.dataservice.application.port.`in`.transaction

import com.fasterxml.jackson.databind.JsonNode
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
    val appendix: JsonNode? = null,
)

@JvmInline
value class CreateTransactionResponse(val transaction: Transaction)

