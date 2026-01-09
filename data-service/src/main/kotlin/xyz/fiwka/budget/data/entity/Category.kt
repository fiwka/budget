package xyz.fiwka.budget.data.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "categories")
class Category(
    @Id
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    val budget: Budget? = null,
    val name: String? = null,
    val multiplier: BigDecimal? = null
)
