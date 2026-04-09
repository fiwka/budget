package xyz.fiwka.budget.dataservice.application.port.out.outbox

import xyz.fiwka.budget.dataservice.domain.outbox.OutboxMessage
import xyz.fiwka.budget.port.Port

interface SaveOutboxMessageOutputPort : Port<OutboxMessage, OutboxMessage>

