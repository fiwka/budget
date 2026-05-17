package xyz.fiwka.budget.dataservice.application.service.transaction

import com.fasterxml.jackson.databind.json.JsonMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.type.ForbiddenException
import xyz.fiwka.budget.dataservice.application.model.outbox.OutboxTypes
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.DeleteTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.access.FindBudgetRoleForUserRequest
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.SaveOutboxMessageOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.DeleteTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetRole
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.domain.outbox.OutboxMessage
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.dataservice.domain.user.User
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class DeleteTransactionServiceTest {

    private val budgetId = UUID.randomUUID()
    private val categoryId = UUID.randomUUID()
    private val transactionId = UUID.randomUUID()
    private val userId = UUID.randomUUID()
    private val transaction = Transaction(
        id = transactionId,
        categoryId = categoryId,
        completedDate = Instant.parse("2026-01-01T10:00:00Z"),
        amount = BigDecimal("10.00"),
    )

    @Test
    fun `should delete transaction and save deleted event`() {
        val deletedIds = mutableListOf<UUID>()
        val savedOutbox = mutableListOf<OutboxMessage>()
        val service = service(
            role = BudgetRole.EDITOR,
            deletePort = object : DeleteTransactionByIdOutputPort {
                override fun execute(request: UUID) {
                    deletedIds += request
                }
            },
            outboxPort = object : SaveOutboxMessageOutputPort {
                override fun execute(request: OutboxMessage): OutboxMessage {
                    savedOutbox += request
                    return request
                }
            },
        )

        service.execute(DeleteTransactionCommand(transactionId, "alex"))

        assertEquals(listOf(transactionId), deletedIds)
        assertEquals(OutboxTypes.TRANSACTION_DELETED_EVENT, savedOutbox.single().type)
        assertEquals("transaction-deleted", savedOutbox.single().topic)
        assertEquals(transactionId.toString(), savedOutbox.single().payload.get("transactionId").asText())
    }

    @Test
    fun `should reject delete without edit permission`() {
        val service = service(role = BudgetRole.READER)

        assertThrows(ForbiddenException::class.java) {
            service.execute(DeleteTransactionCommand(transactionId, "alex"))
        }
    }

    private fun service(
        role: BudgetRole,
        deletePort: DeleteTransactionByIdOutputPort = object : DeleteTransactionByIdOutputPort {
            override fun execute(request: UUID) = error("Should not delete")
        },
        outboxPort: SaveOutboxMessageOutputPort = object : SaveOutboxMessageOutputPort {
            override fun execute(request: OutboxMessage): OutboxMessage = error("Should not save outbox")
        },
    ) = DeleteTransactionService(
        findTransactionByIdOutputPort = object : FindTransactionByIdOutputPort {
            override fun execute(request: UUID): Transaction? = if (request == transactionId) transaction else null
        },
        findCategoryByIdOutputPort = categoryPort(),
        deleteTransactionByIdOutputPort = deletePort,
        saveOutboxMessageOutputPort = outboxPort,
        budgetAccessGuard = BudgetAccessGuard(userPort(), rolePort(role), categoryPort(), object : FindTransactionByIdOutputPort {
            override fun execute(request: UUID): Transaction? = if (request == transactionId) transaction else null
        }),
        jsonMapper = JsonMapper.builder().findAndAddModules().build(),
        transactionDeletedTopic = "transaction-deleted",
        atomicOperationExecutor = object : AtomicOperationExecutor {
            override fun <T> execute(operation: () -> T): T = operation()
        },
    )

    private fun userPort() = object : FindUserByLoginOutputPort {
        override fun execute(request: String): User? = if (request == "alex") User(userId, "alex", "alex@example.com", "hash") else null
    }

    private fun rolePort(role: BudgetRole) = object : FindBudgetRoleForUserOutputPort {
        override fun execute(request: FindBudgetRoleForUserRequest): BudgetRole? =
            if (request.userId == userId && request.budgetId == budgetId) role else null
    }

    private fun categoryPort() = object : FindCategoryByIdOutputPort {
        override fun execute(request: UUID): Category? = if (request == categoryId) Category(categoryId, budgetId, "Food", true) else null
    }
}
