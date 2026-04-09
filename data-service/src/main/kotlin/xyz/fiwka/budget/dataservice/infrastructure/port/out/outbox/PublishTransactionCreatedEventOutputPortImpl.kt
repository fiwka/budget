package xyz.fiwka.budget.dataservice.infrastructure.port.out.outbox

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishTransactionCreatedEventCommand
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishTransactionCreatedEventOutputPort
import xyz.fiwka.budget.model.event.transaction.TransactionCreatedEvent
import java.time.Instant

@Component
class PublishTransactionCreatedEventOutputPortImpl(
    private val kafkaTemplate: KafkaTemplate<String, TransactionCreatedEvent>,
) : PublishTransactionCreatedEventOutputPort {

    override fun execute(request: PublishTransactionCreatedEventCommand) {
        val payload = request.payload
        val event = TransactionCreatedEvent().apply {
            transactionId = payload.transactionId
            categoryId = payload.categoryId
            completedDate = Instant.ofEpochMilli(payload.completedDate)
            amount = payload.amount
            appendix = payload.appendix
        }

        kafkaTemplate.send(request.topic, payload.transactionId.toString(), event).get()
    }
}
