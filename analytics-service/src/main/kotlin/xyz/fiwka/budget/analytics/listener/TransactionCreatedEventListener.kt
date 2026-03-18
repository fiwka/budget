package xyz.fiwka.budget.analytics.listener

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import xyz.fiwka.budget.model.event.transaction.TransactionCreatedEvent

@Component
class TransactionCreatedEventListener {

    @KafkaListener(
        topics = ["outbox.transaction-created"]
    )
    fun handleTransactioCreatedEvent(
        @Payload event: TransactionCreatedEvent
    ) {
        log.info("Received event: {}", event)
    }

    companion object {
        private val log = LoggerFactory.getLogger(TransactionCreatedEventListener::class.java)
    }
}