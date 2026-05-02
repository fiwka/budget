package xyz.fiwka.budget.dataservice.application.service.outbox

import org.slf4j.LoggerFactory
import xyz.fiwka.budget.dataservice.application.port.`in`.outbox.PublishOutboxMessagesUseCase
import xyz.fiwka.budget.dataservice.application.port.out.outbox.DeleteOutboxMessagesByIdsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.FindOutboxMessagesBatchOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishOutboxEventCommand
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishOutboxEventOutputPort

class PublishOutboxMessagesService(
    private val findOutboxMessagesBatchOutputPort: FindOutboxMessagesBatchOutputPort,
    private val publishOutboxEventOutputPort: PublishOutboxEventOutputPort,
    private val deleteOutboxMessagesByIdsOutputPort: DeleteOutboxMessagesByIdsOutputPort,
    private val batchSize: Int,
) : PublishOutboxMessagesUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(request: Unit): Int {
        val messages = findOutboxMessagesBatchOutputPort.execute(batchSize)
        if (messages.isEmpty()) {
            return 0
        }

        val successfullyPublishedIds = mutableListOf<java.util.UUID>()

        messages.forEach { message ->
            runCatching {
                publishOutboxEventOutputPort.execute(
                    PublishOutboxEventCommand(
                        type = message.type,
                        topic = message.topic,
                        payload = message.payload,
                    )
                )

                successfullyPublishedIds.add(requireNotNull(message.id))
            }.onFailure { error ->
                log.warn("Failed to publish outbox message {} with type {}", message.id, message.type, error)
            }
        }

        if (successfullyPublishedIds.isNotEmpty()) {
            deleteOutboxMessagesByIdsOutputPort.execute(successfullyPublishedIds)
        }

        return successfullyPublishedIds.size
    }
}

