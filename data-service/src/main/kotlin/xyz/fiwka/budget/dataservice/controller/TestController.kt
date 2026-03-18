package xyz.fiwka.budget.dataservice.controller

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.fiwka.budget.model.event.transaction.TransactionCreatedEvent
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/test")
class TestController(
    private val kafkaTemplate: KafkaTemplate<String, TransactionCreatedEvent>
) {

    @GetMapping
    fun test() {
        kafkaTemplate.send(
            "outbox.transaction-created",
            TransactionCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ZonedDateTime.now(),
                BigDecimal("123.45"),
                "Test transaction"
            )
        ).get(10L, TimeUnit.SECONDS)
    }
}