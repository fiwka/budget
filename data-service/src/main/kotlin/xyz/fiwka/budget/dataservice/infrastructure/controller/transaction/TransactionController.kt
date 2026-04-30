package xyz.fiwka.budget.dataservice.infrastructure.controller.transaction

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.CreateTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.CreateTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.DeleteTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.DeleteTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ListBudgetTransactionsCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ListBudgetTransactionsUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.UpdateTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.UpdateTransactionUseCase
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.transaction.TransactionFieldsRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.transaction.TransactionListQueryRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.transaction.TransactionResponse
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.page.PageResponse
import xyz.fiwka.budget.dataservice.infrastructure.mapper.TransactionMapper
import java.util.UUID

@RestController
@Validated
@RequestMapping("/api/transaction")
class TransactionController(
    private val transactionMapper: TransactionMapper,
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val readTransactionUseCase: ReadTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val listBudgetTransactionsUseCase: ListBudgetTransactionsUseCase,
    private val objectMapper: ObjectMapper,
) {

    @PostMapping
    fun createTransaction(
        @Valid @RequestBody transactionFieldsRequest: TransactionFieldsRequest,
        authentication: Authentication,
    ) =
        transactionMapper.toDto(
            createTransactionUseCase.execute(
                CreateTransactionCommand(
                    categoryId = transactionFieldsRequest.categoryId,
                    completedDate = transactionFieldsRequest.completedDate,
                    amount = transactionFieldsRequest.amount,
                    actorLogin = authentication.name,
                    appendix = transactionFieldsRequest.appendix?.let { jsonNodeToMap(it) },
                )
            ).transaction
        )

    @GetMapping("/{id}")
    fun readTransaction(@PathVariable id: UUID, authentication: Authentication) =
        transactionMapper.toDto(readTransactionUseCase.execute(ReadTransactionCommand(id, authentication.name)).transaction)

    @GetMapping("/budget/{budgetId}")
    fun listBudgetTransactions(
        @PathVariable budgetId: UUID,
        @Valid @ModelAttribute query: TransactionListQueryRequest,
        authentication: Authentication,
    ): PageResponse<TransactionResponse> {
        val response = listBudgetTransactionsUseCase.execute(
            ListBudgetTransactionsCommand(
                budgetId = budgetId,
                actorLogin = authentication.name,
                page = query.page,
                size = query.size,
                id = query.id,
                categoryId = query.categoryId,
                completedDateFrom = query.completedDateFrom,
                completedDateTo = query.completedDateTo,
                amountFrom = query.amountFrom,
                amountTo = query.amountTo,
                appendixContains = query.appendixContains,
            )
        ).transactions

        return PageResponse(
            items = response.items.map(transactionMapper::toDto),
            page = response.page,
            size = response.size,
            totalElements = response.totalElements,
            totalPages = response.totalPages,
        )
    }

    @PutMapping("/{id}")
    fun updateTransaction(
        @PathVariable id: UUID,
        @Valid @RequestBody transactionFieldsRequest: TransactionFieldsRequest,
        authentication: Authentication,
    ) =
        transactionMapper.toDto(
            updateTransactionUseCase.execute(
                UpdateTransactionCommand(
                    id = id,
                    categoryId = transactionFieldsRequest.categoryId,
                    completedDate = transactionFieldsRequest.completedDate,
                    amount = transactionFieldsRequest.amount,
                    actorLogin = authentication.name,
                    appendix = transactionFieldsRequest.appendix?.let { jsonNodeToMap(it) },
                )
            ).transaction
        )

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTransaction(@PathVariable id: UUID, authentication: Authentication) {
        deleteTransactionUseCase.execute(DeleteTransactionCommand(id, authentication.name))
    }

    private fun jsonNodeToMap(jsonNode: JsonNode): Map<String, Any>? {
        if (jsonNode.isNull) return null
        return objectMapper.convertValue(jsonNode, object : TypeReference<Map<String, Any>>() {})
    }
}

