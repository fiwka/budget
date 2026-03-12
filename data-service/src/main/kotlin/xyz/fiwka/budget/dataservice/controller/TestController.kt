package xyz.fiwka.budget.dataservice.controller

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tools.jackson.databind.json.JsonMapper
import xyz.fiwka.budget.dataservice.model.event.TransactionCreatedEvent
import java.util.UUID
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/test")
class TestController(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val jsonMapper: JsonMapper
) {

    @GetMapping
    fun test() {
        kafkaTemplate.send("outbox.transaction-created", jsonMapper.writeValueAsString(
            TransactionCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                java.time.ZonedDateTime.now(),
                java.math.BigDecimal("123.45"),
                "Test transaction"
            )
        )).get(10L, TimeUnit.SECONDS)
    }
}