package xyz.fiwka.budget.dataservice.application.port.`in`.transaction

import xyz.fiwka.budget.port.Port
import java.util.UUID

interface DeleteTransactionUseCase : Port<DeleteTransactionCommand, Unit>

@JvmInline
value class DeleteTransactionCommand(val id: UUID)

