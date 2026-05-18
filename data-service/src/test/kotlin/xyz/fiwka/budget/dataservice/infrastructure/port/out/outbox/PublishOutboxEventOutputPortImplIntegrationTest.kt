package xyz.fiwka.budget.dataservice.infrastructure.port.out.outbox

import com.fasterxml.jackson.databind.json.JsonMapper
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import xyz.fiwka.budget.dataservice.application.model.outbox.OutboxTypes
import xyz.fiwka.budget.dataservice.application.model.outbox.TransactionDeletedOutboxPayload
import xyz.fiwka.budget.dataservice.application.model.outbox.TransactionEventOutboxPayload
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishOutboxEventCommand
import xyz.fiwka.budget.model.event.transaction.TransactionCreatedEvent
import xyz.fiwka.budget.model.event.transaction.TransactionDeletedEvent
import java.time.Instant
import java.util.UUID

@SpringJUnitConfig
@EmbeddedKafka(
    partitions = 1,
    topics = ["transaction-created", "transaction-deleted"],
    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
)
class PublishOutboxEventOutputPortImplIntegrationTest @Autowired constructor(
    private val embeddedKafkaBroker: EmbeddedKafkaBroker,
) {
    private val consumers = mutableListOf<AutoCloseable>()

    @AfterEach
    fun closeConsumers() {
        consumers.forEach(AutoCloseable::close)
        consumers.clear()
    }

    @Test
    fun `should publish transaction created event to kafka`() {
        val jsonMapper = jsonMapper()
        val transactionId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val budgetId = UUID.randomUUID()
        val completedDate = Instant.parse("2026-01-05T10:00:00Z")
        val port = port(jsonMapper)
        val consumer = consumer("created-consumer")

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "transaction-created")

        port.execute(
            PublishOutboxEventCommand(
                type = OutboxTypes.TRANSACTION_CREATED_EVENT,
                topic = "transaction-created",
                payload = jsonMapper.valueToTree(
                    TransactionEventOutboxPayload(
                        transactionId = transactionId,
                        categoryId = categoryId,
                        budgetId = budgetId,
                        isConsumption = true,
                        completedDate = completedDate.toEpochMilli(),
                        amount = "42.50",
                        appendix = """{"source":"test"}""",
                    )
                ),
            )
        )

        val record = KafkaTestUtils.getSingleRecord(consumer, "transaction-created")
        val event = record.value() as TransactionCreatedEvent
        assertEquals(transactionId.toString(), record.key())
        assertEquals(transactionId, event.transactionId)
        assertEquals(categoryId, event.categoryId)
        assertEquals(budgetId, event.budgetId)
        assertEquals(true, event.isConsumption)
        assertEquals(completedDate, event.completedDate)
        assertEquals("42.50", event.amount)
        assertEquals("""{"source":"test"}""", event.appendix)
    }

    @Test
    fun `should publish transaction deleted event to kafka`() {
        val jsonMapper = jsonMapper()
        val transactionId = UUID.randomUUID()
        val port = port(jsonMapper)
        val consumer = consumer("deleted-consumer")

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "transaction-deleted")

        port.execute(
            PublishOutboxEventCommand(
                type = OutboxTypes.TRANSACTION_DELETED_EVENT,
                topic = "transaction-deleted",
                payload = jsonMapper.valueToTree(TransactionDeletedOutboxPayload(transactionId)),
            )
        )

        val record = KafkaTestUtils.getSingleRecord(consumer, "transaction-deleted")
        val event = record.value() as TransactionDeletedEvent
        assertEquals(transactionId.toString(), record.key())
        assertEquals(transactionId, event.transactionId)
    }

    private fun port(jsonMapper: JsonMapper): PublishOutboxEventOutputPortImpl =
        PublishOutboxEventOutputPortImpl(
            kafkaTemplate = kafkaTemplate(),
            jsonMapper = jsonMapper,
        )

    private fun kafkaTemplate(): KafkaTemplate<String, Any> {
        val producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker)
        producerProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        producerProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        producerProps["schema.registry.url"] = MOCK_SCHEMA_REGISTRY_URL
        return KafkaTemplate(DefaultKafkaProducerFactory(producerProps))
    }

    private fun consumer(groupId: String): org.apache.kafka.clients.consumer.Consumer<String, Any> {
        val consumerProps = KafkaTestUtils.consumerProps(groupId, "true", embeddedKafkaBroker)
        consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        consumerProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = KafkaAvroDeserializer::class.java
        consumerProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        consumerProps["schema.registry.url"] = MOCK_SCHEMA_REGISTRY_URL
        consumerProps["specific.avro.reader"] = true
        return DefaultKafkaConsumerFactory<String, Any>(consumerProps)
            .createConsumer()
            .also(consumers::add)
    }

    private fun jsonMapper(): JsonMapper =
        JsonMapper.builder()
            .findAndAddModules()
            .build()

    companion object {
        private const val MOCK_SCHEMA_REGISTRY_URL = "mock://publish-outbox-event-output-port-test"
    }
}
