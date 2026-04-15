package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.outbox

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.json.JsonMapper
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
        jsonMapper: JsonMapper,
        @Value("\${app.outbox.batch-size:100}") outboxBatchSize: Int,
    ): PublishOutboxMessagesUseCase =
        PublishOutboxMessagesService(
            findOutboxMessagesBatchOutputPort,
            publishTransactionCreatedEventOutputPort,
            deleteOutboxMessagesByIdsOutputPort,
            jsonMapper,
            outboxBatchSize,
        )
}

