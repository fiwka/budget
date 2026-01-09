package xyz.fiwka.budget.data.entity

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize

@Entity
@Table(name = "budgets")
class Budget(
    @Id
    val id: Long? = null,
    val name: String? = null,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "budget_data_sources",
        joinColumns = [JoinColumn(name = "budget_id")],
        inverseJoinColumns = [JoinColumn(name = "data_source_id")]
    )
    @BatchSize(size = 20)
    val dataSources: MutableSet<DataSource> = mutableSetOf(),
    @OneToMany(mappedBy = "budget", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    val categories: MutableSet<Category> = mutableSetOf(),
    @OneToMany(mappedBy = "budget", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    val userRoles: MutableSet<BudgetUserRole> = mutableSetOf()
)