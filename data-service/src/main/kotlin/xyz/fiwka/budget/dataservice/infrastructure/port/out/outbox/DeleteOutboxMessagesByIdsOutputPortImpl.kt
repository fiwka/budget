package xyz.fiwka.budget.dataservice.infrastructure.port.out.outbox

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.outbox.DeleteOutboxMessagesByIdsOutputPort
import xyz.fiwka.budget.dataservice.infrastructure.repository.OutboxRepository
import java.util.UUID

@Component
class DeleteOutboxMessagesByIdsOutputPortImpl(
    private val outboxRepository: OutboxRepository,
) : DeleteOutboxMessagesByIdsOutputPort {
    override fun execute(request: List<UUID>) {
        outboxRepository.deleteAllByIdInBatch(request)
    }
}

