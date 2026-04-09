package xyz.fiwka.budget.dataservice.infrastructure.port.out.outbox

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.outbox.SaveOutboxMessageOutputPort
import xyz.fiwka.budget.dataservice.domain.outbox.OutboxMessage
import xyz.fiwka.budget.dataservice.infrastructure.entity.OutboxEntity
import xyz.fiwka.budget.dataservice.infrastructure.repository.OutboxRepository

@Component
class SaveOutboxMessageOutputPortImpl(
    private val outboxRepository: OutboxRepository,
) : SaveOutboxMessageOutputPort {
    override fun execute(request: OutboxMessage): OutboxMessage {
        val entity = OutboxEntity().apply {
            type = request.type
            topic = request.topic
            payload = request.payload
        }

        val savedEntity = outboxRepository.save(entity)
        return OutboxMessage(
            id = savedEntity.id,
            type = savedEntity.type,
            topic = savedEntity.topic,
            payload = savedEntity.payload,
            createdAt = savedEntity.createdAt,
        )
    }
}

