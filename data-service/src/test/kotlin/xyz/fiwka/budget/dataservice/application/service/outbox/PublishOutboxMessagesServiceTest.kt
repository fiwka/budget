package xyz.fiwka.budget.dataservice.application.service.outbox

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import com.fasterxml.jackson.databind.json.JsonMapper
import xyz.fiwka.budget.dataservice.application.model.outbox.OutboxTypes
import xyz.fiwka.budget.dataservice.application.model.outbox.TransactionEventOutboxPayload
import xyz.fiwka.budget.dataservice.application.port.out.outbox.DeleteOutboxMessagesByIdsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.FindOutboxMessagesBatchOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishOutboxEventCommand
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishOutboxEventOutputPort
import xyz.fiwka.budget.dataservice.domain.outbox.OutboxMessage
import java.time.Instant
import java.util.UUID

class PublishOutboxMessagesServiceTest {

    @Test
    fun `should publish transaction created messages and delete from outbox`() {
        val jsonMapper = JsonMapper.builder()
            .findAndAddModules()
            .build()
        val messageId = UUID.randomUUID()
        val payload = TransactionEventOutboxPayload(
            transactionId = UUID.randomUUID(),
            categoryId = UUID.randomUUID(),
            budgetId = UUID.randomUUID(),
            isConsumption = true,
            completedDate = Instant.now().toEpochMilli(),
            amount = "10.00",
            appendix = "null",
        )

        val findBatchPort = object : FindOutboxMessagesBatchOutputPort {
            override fun execute(request: Int): List<OutboxMessage> {
                return listOf(
                    OutboxMessage(
                        id = messageId,
                        type = OutboxTypes.TRANSACTION_CREATED_EVENT,
                        topic = "transaction-created",
                        payload = jsonMapper.valueToTree(payload),
                        createdAt = Instant.now(),
                    )
                )
            }
        }

        val publishedTopics = mutableListOf<String>()
        val publishPort = object : PublishOutboxEventOutputPort {
            override fun execute(request: PublishOutboxEventCommand) {
                publishedTopics.add(request.topic)
            }
        }

        val deletedIds = mutableListOf<UUID>()
        val deletePort = object : DeleteOutboxMessagesByIdsOutputPort {
            override fun execute(request: List<UUID>) {
                deletedIds.addAll(request)
            }
        }

        val service = PublishOutboxMessagesService(
            findOutboxMessagesBatchOutputPort = findBatchPort,
            publishOutboxEventOutputPort = publishPort,
            deleteOutboxMessagesByIdsOutputPort = deletePort,
            batchSize = 50,
        )

        val result = service.execute(Unit)

        assertEquals(1, result)
        assertEquals(listOf("transaction-created"), publishedTopics)
        assertEquals(listOf(messageId), deletedIds)
    }
}


