package xyz.fiwka.budget.analytics.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xyz.fiwka.budget.analytics.infrastructure.entity.ProcessedEventEntity
import xyz.fiwka.budget.analytics.infrastructure.repository.ProcessedEventRepository
import xyz.fiwka.budget.model.event.transaction.TransactionCreatedEvent
import xyz.fiwka.budget.model.event.transaction.TransactionDeletedEvent
import xyz.fiwka.budget.model.event.transaction.TransactionUpdatedEvent
import java.math.BigDecimal

@Service
class TransactionEventProcessingService(
    private val budgetAnalyticsService: BudgetAnalyticsService,
    private val processedEventRepository: ProcessedEventRepository,
) {

    @Transactional
    fun processCreated(event: TransactionCreatedEvent) {
        if (processedEventRepository.existsById(event.transactionId.toString())) return
        budgetAnalyticsService.upsertSnapshot(
            transactionId = event.transactionId,
            budgetId = event.budgetId,
            categoryId = event.categoryId,
            completedDate = event.completedDate,
            amount = BigDecimal(event.amount),
            isConsumption = event.isConsumption,
        )
        processedEventRepository.save(ProcessedEventEntity(event.transactionId.toString()))
    }

    @Transactional
    fun processUpdated(event: TransactionUpdatedEvent) {
        val eventId = "${event.transactionId}:updated:${event.completedDate.toEpochMilli()}:${event.amount}:${event.categoryId}"
        if (processedEventRepository.existsById(eventId)) return
        budgetAnalyticsService.upsertSnapshot(
            transactionId = event.transactionId,
            budgetId = event.budgetId,
            categoryId = event.categoryId,
            completedDate = event.completedDate,
            amount = BigDecimal(event.amount),
            isConsumption = event.isConsumption,
        )
        processedEventRepository.save(ProcessedEventEntity(eventId))
    }

    @Transactional
    fun processDeleted(event: TransactionDeletedEvent) {
        val eventId = "${event.transactionId}:deleted"
        if (processedEventRepository.existsById(eventId)) return
        budgetAnalyticsService.deleteSnapshot(event.transactionId)
        processedEventRepository.save(ProcessedEventEntity(eventId))
    }
}
