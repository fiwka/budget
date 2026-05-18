package xyz.fiwka.budget.analytics.infrastructure.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import xyz.fiwka.budget.analytics.application.model.BudgetAnalyticsSummary
import xyz.fiwka.budget.analytics.application.service.BudgetAnalyticsService
import java.math.BigDecimal
import java.time.YearMonth
import java.util.UUID

class AnalyticsControllerTest {

    @Test
    fun `should use requested period`() {
        val budgetId = UUID.randomUUID()
        val service = mock(BudgetAnalyticsService::class.java)
        `when`(service.getMonthlySummary(budgetId, 2026, 2)).thenReturn(summary(budgetId, "2026-02"))
        val controller = AnalyticsController(service)

        val response = controller.getBudgetMonthlySummary(budgetId, "2026-02")

        verify(service).getMonthlySummary(budgetId, 2026, 2)
        assertEquals("2026-02", response.period)
    }

    @Test
    fun `should default to current period`() {
        val budgetId = UUID.randomUUID()
        val service = mock(BudgetAnalyticsService::class.java)
        val current = YearMonth.now()
        `when`(service.getMonthlySummary(budgetId, current.year, current.monthValue)).thenReturn(summary(budgetId, "current"))
        val controller = AnalyticsController(service)

        val response = controller.getBudgetMonthlySummary(budgetId, null)

        assertEquals(budgetId, response.budgetId)
        assertEquals("current", response.period)
    }

    private fun summary(budgetId: UUID, period: String): BudgetAnalyticsSummary =
        BudgetAnalyticsSummary(
            budgetId = budgetId,
            period = period,
            income = BigDecimal.ZERO,
            expenses = BigDecimal.ZERO,
            balance = BigDecimal.ZERO,
            savingsRate = BigDecimal.ZERO,
            topExpenseCategories = emptyList(),
        )

}
