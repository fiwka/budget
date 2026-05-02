package xyz.fiwka.budget.analytics.infrastructure.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.fiwka.budget.analytics.application.model.BudgetAnalyticsSummary
import xyz.fiwka.budget.analytics.application.service.BudgetAnalyticsService
import java.time.YearMonth
import java.util.UUID

@RestController
@RequestMapping("/api/analytics")
class AnalyticsController(
    private val budgetAnalyticsService: BudgetAnalyticsService,
) {
    @GetMapping("/budget/monthly-summary")
    fun getBudgetMonthlySummary(
        @RequestParam budgetId: UUID,
        @RequestParam(required = false) period: String?,
    ): BudgetAnalyticsSummary {
        val target = period?.let(YearMonth::parse) ?: YearMonth.now()
        return budgetAnalyticsService.getMonthlySummary(budgetId, target.year, target.monthValue)
    }
}
