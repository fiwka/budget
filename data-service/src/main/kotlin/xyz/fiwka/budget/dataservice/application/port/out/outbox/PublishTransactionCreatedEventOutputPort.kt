package xyz.fiwka.budget.dataservice.application.port.out.outbox

import xyz.fiwka.budget.dataservice.application.model.outbox.TransactionCreatedOutboxPayload
import xyz.fiwka.budget.port.Port

interface PublishTransactionCreatedEventOutputPort : Port<PublishTransactionCreatedEventCommand, Unit>

data class PublishTransactionCreatedEventCommand(
    val topic: String,
    val payload: TransactionCreatedOutboxPayload,
)

