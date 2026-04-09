package xyz.fiwka.budget.dataservice.application.port.out.outbox

import xyz.fiwka.budget.port.Port
import java.util.UUID

interface DeleteOutboxMessagesByIdsOutputPort : Port<List<UUID>, Unit>

