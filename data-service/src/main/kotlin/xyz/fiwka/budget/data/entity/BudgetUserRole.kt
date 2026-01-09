package xyz.fiwka.budget.data.entity

import jakarta.persistence.*

@Entity
@Table(name = "budget_user_roles")
class BudgetUserRole(
    @Id
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    val budget: Budget? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User? = null,
    val role: Int? = null
)

