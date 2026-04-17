package xyz.fiwka.budget.dataservice.application.port.`in`.transaction

import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface ReadTransactionUseCase : Port<ReadTransactionCommand, ReadTransactionResponse>

data class ReadTransactionCommand(
	val id: UUID,
	val actorLogin: String,
)

@JvmInline
value class ReadTransactionResponse(val transaction: Transaction)

