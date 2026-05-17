package xyz.fiwka.budget.analytics.application.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import xyz.fiwka.budget.analytics.infrastructure.PostgresIntegrationTest
import xyz.fiwka.budget.analytics.infrastructure.repository.AnalyticsTransactionSnapshotRepository
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@DataJpaTest
@Import(BudgetAnalyticsService::class)
class BudgetAnalyticsServiceIntegrationTest @Autowired constructor(
    private val service: BudgetAnalyticsService,
    private val repository: AnalyticsTransactionSnapshotRepository,
) : PostgresIntegrationTest() {

    @Test
    fun `should calculate monthly summary and category shares`() {
        val budgetId = UUID.randomUUID()
        val foodCategoryId = UUID.randomUUID()
        val transportCategoryId = UUID.randomUUID()
        service.upsertSnapshot(UUID.randomUUID(), budgetId, foodCategoryId, Instant.parse("2026-01-05T10:00:00Z"), BigDecimal("40.00"), true)
        service.upsertSnapshot(UUID.randomUUID(), budgetId, foodCategoryId, Instant.parse("2026-01-06T10:00:00Z"), BigDecimal("60.00"), true)
        service.upsertSnapshot(UUID.randomUUID(), budgetId, transportCategoryId, Instant.parse("2026-01-07T10:00:00Z"), BigDecimal("50.00"), true)
        service.upsertSnapshot(UUID.randomUUID(), budgetId, UUID.randomUUID(), Instant.parse("2026-01-08T10:00:00Z"), BigDecimal("500.00"), false)
        service.upsertSnapshot(UUID.randomUUID(), budgetId, foodCategoryId, Instant.parse("2026-02-01T00:00:00Z"), BigDecimal("999.00"), true)

        val summary = service.getMonthlySummary(budgetId, 2026, 1)

        assertEquals(BigDecimal("500.00"), summary.income)
        assertEquals(BigDecimal("150.00"), summary.expenses)
        assertEquals(BigDecimal("350.00"), summary.balance)
        assertEquals(BigDecimal("70.00"), summary.savingsRate)
        assertEquals(listOf(foodCategoryId, transportCategoryId), summary.topExpenseCategories.map { it.categoryId })
        assertEquals(BigDecimal("66.67"), summary.topExpenseCategories.first().sharePercent)
    }

    @Test
    fun `should delete existing snapshot and return null for missing snapshot`() {
        val transactionId = UUID.randomUUID()
        service.upsertSnapshot(
            transactionId = transactionId,
            budgetId = UUID.randomUUID(),
            categoryId = UUID.randomUUID(),
            completedDate = Instant.parse("2026-01-05T10:00:00Z"),
            amount = BigDecimal("10.00"),
            isConsumption = true,
        )

        val deleted = service.deleteSnapshot(transactionId)
        val missing = service.deleteSnapshot(transactionId)

        assertEquals(transactionId, deleted?.transactionId)
        assertEquals(false, repository.existsById(transactionId))
        assertEquals(null, missing)
    }
}
