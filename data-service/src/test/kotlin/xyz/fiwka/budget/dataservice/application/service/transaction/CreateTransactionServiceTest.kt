package xyz.fiwka.budget.dataservice.application.service.transaction

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.model.outbox.OutboxTypes
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.CreateTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.SaveOutboxMessageOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.SaveTransactionOutputPort
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.domain.outbox.OutboxMessage
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class CreateTransactionServiceTest {

    @Test
    fun `should create transaction and save outbox message atomically`() {
        val categoryId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val completedDate = Instant.parse("2026-01-01T10:00:00Z")

        val findCategoryPort = object : FindCategoryByIdOutputPort {
            override fun execute(request: UUID): Category? {
                return if (request == categoryId) Category(UUID.randomUUID(), UUID.randomUUID(), "Food", true) else null
            }
        }

        val saveTransactionPort = object : SaveTransactionOutputPort {
            override fun execute(request: Transaction): Transaction {
                return Transaction(
                    id = transactionId,
                    categoryId = request.categoryId,
                    completedDate = request.completedDate,
                    amount = request.amount,
                    appendix = request.appendix,
                )
            }
        }

        var savedOutbox: OutboxMessage? = null
        val saveOutboxPort = object : SaveOutboxMessageOutputPort {
            override fun execute(request: OutboxMessage): OutboxMessage {
                savedOutbox = request
                return OutboxMessage(
                    id = UUID.randomUUID(),
                    type = request.type,
                    topic = request.topic,
                    payload = request.payload,
                    createdAt = Instant.now(),
                )
            }
        }

        val service = CreateTransactionService(
            findCategoryByIdOutputPort = findCategoryPort,
            saveTransactionOutputPort = saveTransactionPort,
            saveOutboxMessageOutputPort = saveOutboxPort,
            objectMapper = ObjectMapper(),
            transactionCreatedTopic = "transaction-created",
            atomicOperationExecutor = object : AtomicOperationExecutor {
                override fun <T> execute(operation: () -> T): T = operation()
            },
        )

        val response = service.execute(
            CreateTransactionCommand(
                categoryId = categoryId,
                completedDate = completedDate,
                amount = BigDecimal("99.50"),
                appendix = null,
            )
        )

        assertEquals(transactionId, response.transaction.id)
        assertNotNull(savedOutbox)
        assertEquals(OutboxTypes.TRANSACTION_CREATED_EVENT, savedOutbox!!.type)
        assertEquals("transaction-created", savedOutbox.topic)
        assertEquals(transactionId.toString(), savedOutbox.payload.get("transactionId").asText())
    }
}


