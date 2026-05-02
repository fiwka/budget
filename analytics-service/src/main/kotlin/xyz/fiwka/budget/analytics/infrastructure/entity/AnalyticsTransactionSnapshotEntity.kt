package xyz.fiwka.budget.analytics.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "analytics_transaction_snapshots")
class AnalyticsTransactionSnapshotEntity(
    @Id
    @Column(name = "transaction_id", nullable = false)
    var transactionId: UUID,
    @Column(name = "budget_id", nullable = false)
    var budgetId: UUID,
    @Column(name = "category_id", nullable = false)
    var categoryId: UUID,
    @Column(name = "completed_date", nullable = false)
    var completedDate: Instant,
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    var amount: BigDecimal,
    @Column(name = "is_consumption", nullable = false)
    var isConsumption: Boolean,
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
) {
    constructor() : this(
        transactionId = UUID.randomUUID(),
        budgetId = UUID.randomUUID(),
        categoryId = UUID.randomUUID(),
        completedDate = Instant.now(),
        amount = BigDecimal.ZERO,
        isConsumption = false,
        updatedAt = Instant.now(),
    )
}
