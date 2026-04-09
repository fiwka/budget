package xyz.fiwka.budget.dataservice.application.service.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import xyz.fiwka.budget.dataservice.application.model.outbox.OutboxTypes
import xyz.fiwka.budget.dataservice.application.model.outbox.TransactionCreatedOutboxPayload
import xyz.fiwka.budget.dataservice.application.port.`in`.outbox.PublishOutboxMessagesUseCase
import xyz.fiwka.budget.dataservice.application.port.out.outbox.DeleteOutboxMessagesByIdsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.FindOutboxMessagesBatchOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishTransactionCreatedEventCommand
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishTransactionCreatedEventOutputPort

class PublishOutboxMessagesService(
    private val findOutboxMessagesBatchOutputPort: FindOutboxMessagesBatchOutputPort,
    private val publishTransactionCreatedEventOutputPort: PublishTransactionCreatedEventOutputPort,
    private val deleteOutboxMessagesByIdsOutputPort: DeleteOutboxMessagesByIdsOutputPort,
    private val objectMapper: ObjectMapper,
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
                if (message.type != OutboxTypes.TRANSACTION_CREATED_EVENT) {
                    log.warn("Skipping unsupported outbox message type '{}' for message {}", message.type, message.id)
                    return@runCatching
                }

                val payload = objectMapper.treeToValue(message.payload, TransactionCreatedOutboxPayload::class.java)
                publishTransactionCreatedEventOutputPort.execute(
                    PublishTransactionCreatedEventCommand(
                        topic = message.topic,
                        payload = payload,
                    )
                )

                successfullyPublishedIds.add(requireNotNull(message.id))
            }.onFailure { error ->
                log.warn("Failed to publish outbox message {}", message.id, error)
            }
        }

        if (successfullyPublishedIds.isNotEmpty()) {
            deleteOutboxMessagesByIdsOutputPort.execute(successfullyPublishedIds)
        }

        return successfullyPublishedIds.size
    }
}

