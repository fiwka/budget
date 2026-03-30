package xyz.fiwka.budget.dataservice.infrastructure.task.outbox

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue
import xyz.fiwka.budget.dataservice.infrastructure.repository.OutboxRepository
import xyz.fiwka.budget.model.event.transaction.TransactionCreatedEvent
import java.util.concurrent.TimeUnit

@Component
class OutboxTransactionCreatedEventTask(
    private val outboxRepository: OutboxRepository,
    private val jsonMapper: JsonMapper,
    private val transactionTemplate: TransactionTemplate,
    private val kafkaTemplate: KafkaTemplate<String, TransactionCreatedEvent>
) {

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.MINUTES)
    fun processOutboxEvents() {
        val events = outboxRepository.findAllByType(
            TransactionCreatedEvent::class.java.name,
            PageRequest.of(
                0,
                MAX_EVENTS,
                Sort.by(Sort.Direction.ASC, "createdAt")
            )
        )

        for (event in events) {
            kafkaTemplate.send(
                TOPIC,
                jsonMapper.readValue(event.payload)
            ).thenRun {
                transactionTemplate.executeWithoutResult {
                    outboxRepository.delete(event)
                }
            }
        }
    }

    companion object {
        private const val MAX_EVENTS = 50
        private const val TOPIC = "outbox.transaction-created"
    }
}