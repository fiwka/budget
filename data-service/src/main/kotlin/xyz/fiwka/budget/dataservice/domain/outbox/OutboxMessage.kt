package xyz.fiwka.budget.dataservice.domain.outbox

import com.fasterxml.jackson.databind.JsonNode
import java.time.Instant
import java.util.UUID

class OutboxMessage(
    val id: UUID?,
    val type: String,
    val topic: String,
    val payload: JsonNode,
    val createdAt: Instant? = null,
)

