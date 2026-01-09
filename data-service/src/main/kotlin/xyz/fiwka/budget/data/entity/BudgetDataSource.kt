package xyz.fiwka.budget.data.entity

import jakarta.persistence.*

@Entity
@Table(name = "budget_data_sources")
class BudgetDataSource(
    @Id
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    val budget: Budget? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_source_id")
    val dataSource: DataSource? = null
)
