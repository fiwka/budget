package xyz.fiwka.budget.dataservice.application.port.`in`.transaction

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
    val actorLogin: String,
    val appendix: Map<String, Any>? = null,
)

@JvmInline
value class UpdateTransactionResponse(val transaction: Transaction)
