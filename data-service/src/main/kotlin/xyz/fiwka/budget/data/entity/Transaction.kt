package xyz.fiwka.budget.data.entity

import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType
import jakarta.persistence.*
import org.hibernate.annotations.CompositeType
import java.time.OffsetDateTime
import javax.money.MonetaryAmount

@Entity
@Table(name = "transactions")
class Transaction(
    @Id
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    val budget: Budget? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    val category: Category? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_source_id")
    val dataSource: DataSource? = null,
    @CompositeType(MonetaryAmountType::class)
    @AttributeOverrides(
        AttributeOverride(name = "amount", column = Column(name = "amount")),
        AttributeOverride(name = "currency", column = Column(name = "currency"))
    )
    val amount: MonetaryAmount? = null,
    val completionDate: OffsetDateTime? = null
)
