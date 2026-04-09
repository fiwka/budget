package xyz.fiwka.budget.dataservice.application.port.out.outbox

import xyz.fiwka.budget.dataservice.domain.outbox.OutboxMessage
import xyz.fiwka.budget.port.Port

interface FindOutboxMessagesBatchOutputPort : Port<Int, List<OutboxMessage>>

