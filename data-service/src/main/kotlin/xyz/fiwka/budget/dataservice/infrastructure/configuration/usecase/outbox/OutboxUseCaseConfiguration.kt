package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.outbox

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.dataservice.application.port.`in`.outbox.PublishOutboxMessagesUseCase
import xyz.fiwka.budget.dataservice.application.port.out.outbox.DeleteOutboxMessagesByIdsOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.FindOutboxMessagesBatchOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.PublishOutboxEventOutputPort
import xyz.fiwka.budget.dataservice.application.service.outbox.PublishOutboxMessagesService

@Configuration
class OutboxUseCaseConfiguration {

    @Bean
    fun publishOutboxMessagesUseCase(
        findOutboxMessagesBatchOutputPort: FindOutboxMessagesBatchOutputPort,
        publishOutboxEventOutputPort: PublishOutboxEventOutputPort,
        deleteOutboxMessagesByIdsOutputPort: DeleteOutboxMessagesByIdsOutputPort,
        @Value("\${app.outbox.batch-size:100}") outboxBatchSize: Int,
    ): PublishOutboxMessagesUseCase =
        PublishOutboxMessagesService(
            findOutboxMessagesBatchOutputPort,
            publishOutboxEventOutputPort,
            deleteOutboxMessagesByIdsOutputPort,
            outboxBatchSize,
        )
}

