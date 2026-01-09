package xyz.fiwka.budget.data.entity

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Type

@Entity
@Table(name = "data_sources")
class DataSource(
    @Id
    val id: Long? = null,
    val type: Int? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    val owner: User? = null,
    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    val metadata: Map<String, Any>? = null,
    @ManyToMany(mappedBy = "dataSources", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    val budgets: MutableSet<Budget> = mutableSetOf()
)

