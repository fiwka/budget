package xyz.fiwka.budget.dataservice.application.port.out.outbox

import com.fasterxml.jackson.databind.JsonNode
import xyz.fiwka.budget.port.Port

interface PublishOutboxEventOutputPort : Port<PublishOutboxEventCommand, Unit>

data class PublishOutboxEventCommand(
    val type: String,
    val topic: String,
    val payload: JsonNode,
)
