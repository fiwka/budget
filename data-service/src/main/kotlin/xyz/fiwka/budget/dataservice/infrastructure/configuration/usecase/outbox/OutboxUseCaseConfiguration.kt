package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.dataservice.application.port.`in`.outbox.PublishOutboxMessagesUseCase
import xyz.fiwka.budget.dataservice.application.port.out.outbox.DeleteOutboxMessagesByIdsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.FindOutboxMessagesBatchOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishTransactionCreatedEventOutputPort
import xyz.fiwka.budget.dataservice.application.service.outbox.PublishOutboxMessagesService

@Configuration
class OutboxUseCaseConfiguration {

    @Bean
    fun publishOutboxMessagesUseCase(
        findOutboxMessagesBatchOutputPort: FindOutboxMessagesBatchOutputPort,
        publishTransactionCreatedEventOutputPort: PublishTransactionCreatedEventOutputPort,
        deleteOutboxMessagesByIdsOutputPort: DeleteOutboxMessagesByIdsOutputPort,
        objectMapper: ObjectMapper,
        @Value("\${app.outbox.batch-size:100}") outboxBatchSize: Int,
    ): PublishOutboxMessagesUseCase =
        PublishOutboxMessagesService(
            findOutboxMessagesBatchOutputPort,
            publishTransactionCreatedEventOutputPort,
            deleteOutboxMessagesByIdsOutputPort,
            objectMapper,
            outboxBatchSize,
        )
}

