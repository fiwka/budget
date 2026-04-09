package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.transaction

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.CreateTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.DeleteTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.UpdateTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.SaveOutboxMessageOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.DeleteTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.SaveTransactionOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.UpdateTransactionOutputPort
import xyz.fiwka.budget.dataservice.application.service.transaction.CreateTransactionService
import xyz.fiwka.budget.dataservice.application.service.transaction.DeleteTransactionService
import xyz.fiwka.budget.dataservice.application.service.transaction.ReadTransactionService
import xyz.fiwka.budget.dataservice.application.service.transaction.UpdateTransactionService

@Configuration
class TransactionUseCaseConfiguration {

    @Bean
    fun createTransactionUseCase(
        findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
        saveTransactionOutputPort: SaveTransactionOutputPort,
        saveOutboxMessageOutputPort: SaveOutboxMessageOutputPort,
        objectMapper: ObjectMapper,
        @Value("\${app.kafka.topic.transaction-created:transaction-created}") transactionCreatedTopic: String,
        atomicOperationExecutor: AtomicOperationExecutor,
    ): CreateTransactionUseCase =
        CreateTransactionService(
            findCategoryByIdOutputPort,
            saveTransactionOutputPort,
            saveOutboxMessageOutputPort,
            objectMapper,
            transactionCreatedTopic,
            atomicOperationExecutor,
        )

    @Bean
    fun readTransactionUseCase(findTransactionByIdOutputPort: FindTransactionByIdOutputPort): ReadTransactionUseCase =
        ReadTransactionService(findTransactionByIdOutputPort)

    @Bean
    fun updateTransactionUseCase(
        findTransactionByIdOutputPort: FindTransactionByIdOutputPort,
        findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
        updateTransactionOutputPort: UpdateTransactionOutputPort,
        atomicOperationExecutor: AtomicOperationExecutor,
    ): UpdateTransactionUseCase =
        UpdateTransactionService(
            findTransactionByIdOutputPort,
            findCategoryByIdOutputPort,
            updateTransactionOutputPort,
            atomicOperationExecutor,
        )

    @Bean
    fun deleteTransactionUseCase(
        findTransactionByIdOutputPort: FindTransactionByIdOutputPort,
        deleteTransactionByIdOutputPort: DeleteTransactionByIdOutputPort,
        atomicOperationExecutor: AtomicOperationExecutor,
    ): DeleteTransactionUseCase =
        DeleteTransactionService(findTransactionByIdOutputPort, deleteTransactionByIdOutputPort, atomicOperationExecutor)
}

