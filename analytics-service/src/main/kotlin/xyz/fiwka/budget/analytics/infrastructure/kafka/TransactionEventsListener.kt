package xyz.fiwka.budget.analytics.infrastructure.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import xyz.fiwka.budget.analytics.application.service.TransactionEventProcessingService
import xyz.fiwka.budget.model.event.transaction.TransactionCreatedEvent
import xyz.fiwka.budget.model.event.transaction.TransactionDeletedEvent
import xyz.fiwka.budget.model.event.transaction.TransactionUpdatedEvent

@Component
class TransactionEventsListener(
    private val transactionEventProcessingService: TransactionEventProcessingService,
) {
    @KafkaListener(topics = ["\${app.kafka.topic.transaction-created:transaction-created}"])
    fun onCreated(event: TransactionCreatedEvent) {
        transactionEventProcessingService.processCreated(event)
    }

    @KafkaListener(topics = ["\${app.kafka.topic.transaction-updated:transaction-updated}"])
    fun onUpdated(event: TransactionUpdatedEvent) {
        transactionEventProcessingService.processUpdated(event)
    }

    @KafkaListener(topics = ["\${app.kafka.topic.transaction-deleted:transaction-deleted}"])
    fun onDeleted(event: TransactionDeletedEvent) {
        transactionEventProcessingService.processDeleted(event)
    }
}
