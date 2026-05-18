package xyz.fiwka.budget.dataservice.infrastructure.controller.transaction

import com.fasterxml.jackson.databind.json.JsonMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.TestingAuthenticationToken
import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.CreateTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.CreateTransactionResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.CreateTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.DeleteTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.DeleteTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ListBudgetTransactionsCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ListBudgetTransactionsResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ListBudgetTransactionsUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.UpdateTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.UpdateTransactionResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.UpdateTransactionUseCase
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.transaction.TransactionFieldsRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.transaction.TransactionListQueryRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.transaction.TransactionResponse
import xyz.fiwka.budget.dataservice.infrastructure.entity.TransactionEntity
import xyz.fiwka.budget.dataservice.infrastructure.mapper.TransactionMapper
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class TransactionControllerTest {

    @Test
    fun `should create read update delete and list transactions`() {
        val mapper = JsonMapper.builder().findAndAddModules().build()
        val budgetId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val completedDate = Instant.parse("2026-01-05T10:00:00Z")
        val appendix = mapper.readTree("""{"merchant":"Shop"}""")
        var created: CreateTransactionCommand? = null
        var read: ReadTransactionCommand? = null
        var updated: UpdateTransactionCommand? = null
        var deleted: DeleteTransactionCommand? = null
        var listed: ListBudgetTransactionsCommand? = null
        val controller = TransactionController(
            transactionMapper = transactionMapper(),
            createTransactionUseCase = object : CreateTransactionUseCase {
                override fun execute(request: CreateTransactionCommand): CreateTransactionResponse {
                    created = request
                    return CreateTransactionResponse(Transaction(transactionId, request.categoryId, request.completedDate, request.amount, request.appendix))
                }
            },
            readTransactionUseCase = object : ReadTransactionUseCase {
                override fun execute(request: ReadTransactionCommand): ReadTransactionResponse {
                    read = request
                    return ReadTransactionResponse(Transaction(request.id, categoryId, completedDate, BigDecimal("10.00")))
                }
            },
            updateTransactionUseCase = object : UpdateTransactionUseCase {
                override fun execute(request: UpdateTransactionCommand): UpdateTransactionResponse {
                    updated = request
                    return UpdateTransactionResponse(Transaction(request.id, request.categoryId, request.completedDate, request.amount, request.appendix))
                }
            },
            deleteTransactionUseCase = object : DeleteTransactionUseCase {
                override fun execute(request: DeleteTransactionCommand) {
                    deleted = request
                }
            },
            listBudgetTransactionsUseCase = object : ListBudgetTransactionsUseCase {
                override fun execute(request: ListBudgetTransactionsCommand): ListBudgetTransactionsResponse {
                    listed = request
                    return ListBudgetTransactionsResponse(
                        PageResult(
                            items = listOf(Transaction(transactionId, categoryId, completedDate, BigDecimal("10.00"))),
                            page = request.page,
                            size = request.size,
                            totalElements = 1,
                            totalPages = 1,
                        )
                    )
                }
            },
            objectMapper = mapper,
        )
        val auth = TestingAuthenticationToken("alex", "credentials")

        assertEquals(transactionId, controller.createTransaction(TransactionFieldsRequest(categoryId, completedDate, BigDecimal("10.00"), appendix), auth).id)
        assertEquals(transactionId, controller.readTransaction(transactionId, auth).id)
        assertEquals(BigDecimal("15.00"), controller.updateTransaction(transactionId, TransactionFieldsRequest(categoryId, completedDate, BigDecimal("15.00"), appendix), auth).amount)
        controller.deleteTransaction(transactionId, auth)
        val page = controller.listBudgetTransactions(budgetId, TransactionListQueryRequest(categoryId = categoryId, page = 2, size = 3), auth)

        assertEquals(CreateTransactionCommand(categoryId, completedDate, BigDecimal("10.00"), "alex", mapOf("merchant" to "Shop")), created)
        assertEquals(ReadTransactionCommand(transactionId, "alex"), read)
        assertEquals(UpdateTransactionCommand(transactionId, categoryId, completedDate, BigDecimal("15.00"), "alex", mapOf("merchant" to "Shop")), updated)
        assertEquals(DeleteTransactionCommand(transactionId, "alex"), deleted)
        assertEquals(categoryId, listed?.categoryId)
        assertEquals(1, page.items.size)
    }

    @Test
    fun `should convert null appendix to null command appendix`() {
        val mapper = JsonMapper.builder().findAndAddModules().build()
        val categoryId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val completedDate = Instant.parse("2026-01-05T10:00:00Z")
        var created: CreateTransactionCommand? = null
        val controller = TransactionController(
            transactionMapper = transactionMapper(),
            createTransactionUseCase = object : CreateTransactionUseCase {
                override fun execute(request: CreateTransactionCommand): CreateTransactionResponse {
                    created = request
                    return CreateTransactionResponse(Transaction(transactionId, request.categoryId, request.completedDate, request.amount, request.appendix))
                }
            },
            readTransactionUseCase = object : ReadTransactionUseCase {
                override fun execute(request: ReadTransactionCommand): ReadTransactionResponse =
                    ReadTransactionResponse(Transaction(request.id, categoryId, completedDate, BigDecimal.ONE))
            },
            updateTransactionUseCase = object : UpdateTransactionUseCase {
                override fun execute(request: UpdateTransactionCommand): UpdateTransactionResponse =
                    UpdateTransactionResponse(Transaction(request.id, request.categoryId, request.completedDate, request.amount, request.appendix))
            },
            deleteTransactionUseCase = object : DeleteTransactionUseCase {
                override fun execute(request: DeleteTransactionCommand) = Unit
            },
            listBudgetTransactionsUseCase = object : ListBudgetTransactionsUseCase {
                override fun execute(request: ListBudgetTransactionsCommand): ListBudgetTransactionsResponse =
                    ListBudgetTransactionsResponse(PageResult(emptyList(), request.page, request.size, 0, 0))
            },
            objectMapper = mapper,
        )

        controller.createTransaction(
            TransactionFieldsRequest(categoryId, completedDate, BigDecimal("10.00"), mapper.nullNode()),
            TestingAuthenticationToken("alex", "credentials"),
        )

        assertEquals(null, created?.appendix)
    }

    private fun transactionMapper(): TransactionMapper =
        object : TransactionMapper {
            override fun toEntity(transaction: Transaction): TransactionEntity = TransactionEntity()
            override fun fromEntity(transactionEntity: TransactionEntity): Transaction =
                Transaction(transactionEntity.id, transactionEntity.categoryId, transactionEntity.completedDate, transactionEntity.amount, transactionEntity.appendix)
            override fun toDto(transaction: Transaction): TransactionResponse =
                TransactionResponse(requireNotNull(transaction.id), transaction.categoryId, transaction.completedDate, transaction.amount, transaction.appendix)
        }
}
