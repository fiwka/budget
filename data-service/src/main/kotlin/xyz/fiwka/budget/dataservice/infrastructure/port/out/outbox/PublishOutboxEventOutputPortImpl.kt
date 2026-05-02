package xyz.fiwka.budget.dataservice.infrastructure.port.out.outbox

import com.fasterxml.jackson.databind.json.JsonMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.model.outbox.OutboxTypes
import xyz.fiwka.budget.dataservice.application.model.outbox.TransactionDeletedOutboxPayload
import xyz.fiwka.budget.dataservice.application.model.outbox.TransactionEventOutboxPayload
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishOutboxEventCommand
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishOutboxEventOutputPort
import xyz.fiwka.budget.model.event.transaction.TransactionCreatedEvent
import xyz.fiwka.budget.model.event.transaction.TransactionDeletedEvent
import xyz.fiwka.budget.model.event.transaction.TransactionUpdatedEvent
import java.time.Instant

@Component
class PublishOutboxEventOutputPortImpl(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val jsonMapper: JsonMapper,
) : PublishOutboxEventOutputPort {

    override fun execute(request: PublishOutboxEventCommand) {
        when (request.type) {
            OutboxTypes.TRANSACTION_CREATED_EVENT -> {
                val payload = jsonMapper.treeToValue(request.payload, TransactionEventOutboxPayload::class.java)
                kafkaTemplate.send(request.topic, payload.transactionId.toString(), TransactionCreatedEvent().apply {
                    transactionId = payload.transactionId
                    categoryId = payload.categoryId
                    budgetId = payload.budgetId
                    isConsumption = payload.isConsumption
                    completedDate = Instant.ofEpochMilli(payload.completedDate)
                    amount = payload.amount
                    appendix = payload.appendix
                }).get()
            }
            OutboxTypes.TRANSACTION_UPDATED_EVENT -> {
                val payload = jsonMapper.treeToValue(request.payload, TransactionEventOutboxPayload::class.java)
                kafkaTemplate.send(request.topic, payload.transactionId.toString(), TransactionUpdatedEvent().apply {
                    transactionId = payload.transactionId
                    categoryId = payload.categoryId
                    budgetId = payload.budgetId
                    isConsumption = payload.isConsumption
                    completedDate = Instant.ofEpochMilli(payload.completedDate)
                    amount = payload.amount
                    appendix = payload.appendix
                }).get()
            }
            OutboxTypes.TRANSACTION_DELETED_EVENT -> {
                val payload = jsonMapper.treeToValue(request.payload, TransactionDeletedOutboxPayload::class.java)
                kafkaTemplate.send(request.topic, payload.transactionId.toString(), TransactionDeletedEvent().apply {
                    transactionId = payload.transactionId
                }).get()
            }
            else -> error("Unsupported outbox message type: ${request.type}")
        }
    }
}
