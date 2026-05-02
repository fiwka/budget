package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.transaction

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.fasterxml.jackson.databind.json.JsonMapper
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.CreateTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.DeleteTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ListBudgetTransactionsUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.UpdateTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.SaveOutboxMessageOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.DeleteTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.ListBudgetTransactionsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.SaveTransactionOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.UpdateTransactionOutputPort
import xyz.fiwka.budget.dataservice.application.service.transaction.CreateTransactionService
import xyz.fiwka.budget.dataservice.application.service.transaction.DeleteTransactionService
import xyz.fiwka.budget.dataservice.application.service.transaction.ListBudgetTransactionsService
import xyz.fiwka.budget.dataservice.application.service.transaction.ReadTransactionService
import xyz.fiwka.budget.dataservice.application.service.transaction.UpdateTransactionService
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard

@Configuration
class TransactionUseCaseConfiguration {

    @Bean
    fun createTransactionUseCase(
        findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
        saveTransactionOutputPort: SaveTransactionOutputPort,
        saveOutboxMessageOutputPort: SaveOutboxMessageOutputPort,
        budgetAccessGuard: BudgetAccessGuard,
        jsonMapper: JsonMapper,
        @Value("\${app.kafka.topic.transaction-created:transaction-created}") transactionCreatedTopic: String,
        atomicOperationExecutor: AtomicOperationExecutor,
    ): CreateTransactionUseCase =
        CreateTransactionService(
            findCategoryByIdOutputPort,
            saveTransactionOutputPort,
            saveOutboxMessageOutputPort,
            budgetAccessGuard,
            jsonMapper,
            transactionCreatedTopic,
            atomicOperationExecutor,
        )

    @Bean
    fun readTransactionUseCase(
        findTransactionByIdOutputPort: FindTransactionByIdOutputPort,
        findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
        budgetAccessGuard: BudgetAccessGuard,
    ): ReadTransactionUseCase =
        ReadTransactionService(findTransactionByIdOutputPort, findCategoryByIdOutputPort, budgetAccessGuard)

    @Bean
    fun listBudgetTransactionsUseCase(
        budgetAccessGuard: BudgetAccessGuard,
        listBudgetTransactionsOutputPort: ListBudgetTransactionsOutputPort,
    ): ListBudgetTransactionsUseCase =
        ListBudgetTransactionsService(budgetAccessGuard, listBudgetTransactionsOutputPort)


    @Bean
    fun updateTransactionUseCase(
        findTransactionByIdOutputPort: FindTransactionByIdOutputPort,
        findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
        updateTransactionOutputPort: UpdateTransactionOutputPort,
        saveOutboxMessageOutputPort: SaveOutboxMessageOutputPort,
        budgetAccessGuard: BudgetAccessGuard,
        jsonMapper: JsonMapper,
        @Value("\${app.kafka.topic.transaction-updated:transaction-updated}") transactionUpdatedTopic: String,
        atomicOperationExecutor: AtomicOperationExecutor,
    ): UpdateTransactionUseCase =
        UpdateTransactionService(
            findTransactionByIdOutputPort,
            findCategoryByIdOutputPort,
            updateTransactionOutputPort,
            saveOutboxMessageOutputPort,
            budgetAccessGuard,
            jsonMapper,
            transactionUpdatedTopic,
            atomicOperationExecutor,
        )

    @Bean
    fun deleteTransactionUseCase(
        findTransactionByIdOutputPort: FindTransactionByIdOutputPort,
        findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
        deleteTransactionByIdOutputPort: DeleteTransactionByIdOutputPort,
        saveOutboxMessageOutputPort: SaveOutboxMessageOutputPort,
        budgetAccessGuard: BudgetAccessGuard,
        jsonMapper: JsonMapper,
        @Value("\${app.kafka.topic.transaction-deleted:transaction-deleted}") transactionDeletedTopic: String,
        atomicOperationExecutor: AtomicOperationExecutor,
    ): DeleteTransactionUseCase =
        DeleteTransactionService(
            findTransactionByIdOutputPort,
            findCategoryByIdOutputPort,
            deleteTransactionByIdOutputPort,
            saveOutboxMessageOutputPort,
            budgetAccessGuard,
            jsonMapper,
            transactionDeletedTopic,
            atomicOperationExecutor,
        )
}

