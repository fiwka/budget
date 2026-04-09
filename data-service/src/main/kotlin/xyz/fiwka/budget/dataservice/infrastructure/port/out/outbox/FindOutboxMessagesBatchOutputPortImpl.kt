package xyz.fiwka.budget.dataservice.infrastructure.port.out.outbox

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.outbox.FindOutboxMessagesBatchOutputPort
import xyz.fiwka.budget.dataservice.domain.outbox.OutboxMessage
import xyz.fiwka.budget.dataservice.infrastructure.repository.OutboxRepository

@Component
class FindOutboxMessagesBatchOutputPortImpl(
    private val outboxRepository: OutboxRepository,
) : FindOutboxMessagesBatchOutputPort {
    override fun execute(request: Int): List<OutboxMessage> =
        outboxRepository.findAllByOrderByCreatedAtAsc(PageRequest.of(0, request))
            .map { entity ->
                OutboxMessage(
                    id = entity.id,
                    type = entity.type,
                    topic = entity.topic,
                    payload = entity.payload,
                    createdAt = entity.createdAt,
                )
            }
}

