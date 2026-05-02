package xyz.fiwka.budget.analytics.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import xyz.fiwka.budget.analytics.infrastructure.entity.AnalyticsTransactionSnapshotEntity
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Repository
interface AnalyticsTransactionSnapshotRepository : JpaRepository<AnalyticsTransactionSnapshotEntity, UUID> {
    @Query(
        """
        select coalesce(sum(case when t.isConsumption = true then t.amount else 0 end), 0)
        from AnalyticsTransactionSnapshotEntity t
        where t.budgetId = :budgetId
        and t.completedDate >= :from
        and t.completedDate < :to
        """
    )
    fun sumExpenses(budgetId: UUID, from: Instant, to: Instant): BigDecimal

    @Query(
        """
        select coalesce(sum(case when t.isConsumption = false then t.amount else 0 end), 0)
        from AnalyticsTransactionSnapshotEntity t
        where t.budgetId = :budgetId
        and t.completedDate >= :from
        and t.completedDate < :to
        """
    )
    fun sumIncome(budgetId: UUID, from: Instant, to: Instant): BigDecimal

    @Query(
        """
        select t.categoryId as categoryId, coalesce(sum(t.amount), 0) as total
        from AnalyticsTransactionSnapshotEntity t
        where t.budgetId = :budgetId
        and t.isConsumption = true
        and t.completedDate >= :from
        and t.completedDate < :to
        group by t.categoryId
        order by total desc
        """
    )
    fun findExpenseTotalsByCategory(budgetId: UUID, from: Instant, to: Instant): List<CategoryExpenseTotalProjection>
}

interface CategoryExpenseTotalProjection {
    fun getCategoryId(): UUID
    fun getTotal(): BigDecimal
}
