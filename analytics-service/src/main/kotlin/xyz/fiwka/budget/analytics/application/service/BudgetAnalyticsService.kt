package xyz.fiwka.budget.analytics.application.service

import org.springframework.stereotype.Service
import xyz.fiwka.budget.analytics.application.model.BudgetAnalyticsSummary
import xyz.fiwka.budget.analytics.application.model.CategoryExpenseShare
import xyz.fiwka.budget.analytics.infrastructure.entity.AnalyticsTransactionSnapshotEntity
import xyz.fiwka.budget.analytics.infrastructure.repository.AnalyticsTransactionSnapshotRepository
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneOffset
import java.util.UUID

@Service
class BudgetAnalyticsService(
    private val snapshotRepository: AnalyticsTransactionSnapshotRepository,
) {

    fun upsertSnapshot(
        transactionId: UUID,
        budgetId: UUID,
        categoryId: UUID,
        completedDate: Instant,
        amount: BigDecimal,
        isConsumption: Boolean,
    ) {
        snapshotRepository.save(
            AnalyticsTransactionSnapshotEntity(
                transactionId = transactionId,
                budgetId = budgetId,
                categoryId = categoryId,
                completedDate = completedDate,
                amount = amount,
                isConsumption = isConsumption,
            )
        )
    }

    fun deleteSnapshot(transactionId: UUID): AnalyticsTransactionSnapshotEntity? {
        val existing = snapshotRepository.findById(transactionId).orElse(null) ?: return null
        snapshotRepository.delete(existing)
        return existing
    }

    fun getMonthlySummary(budgetId: UUID, year: Int, month: Int): BudgetAnalyticsSummary {
        val period = YearMonth.of(year, month)
        val from = period.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC)
        val to = period.plusMonths(1).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC)

        val income = snapshotRepository.sumIncome(budgetId, from, to).scaleMoney()
        val expenses = snapshotRepository.sumExpenses(budgetId, from, to).scaleMoney()
        val balance = income.subtract(expenses).scaleMoney()
        val savingsRate = if (income.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal.ZERO
        } else {
            balance.divide(income, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP)
        }

        val topCategories = snapshotRepository.findExpenseTotalsByCategory(budgetId, from, to)
            .take(5)
            .map {
                val total = it.getTotal().scaleMoney()
                val share = if (expenses.compareTo(BigDecimal.ZERO) <= 0) BigDecimal.ZERO else {
                    total.divide(expenses, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(100))
                        .setScale(2, RoundingMode.HALF_UP)
                }
                CategoryExpenseShare(it.getCategoryId(), total, share)
            }

        return BudgetAnalyticsSummary(
            budgetId = budgetId,
            period = period.toString(),
            income = income,
            expenses = expenses,
            balance = balance,
            savingsRate = savingsRate,
            topExpenseCategories = topCategories,
        )
    }

    private fun BigDecimal.scaleMoney(): BigDecimal = setScale(2, RoundingMode.HALF_UP)
}
