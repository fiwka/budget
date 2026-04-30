package xyz.fiwka.budget.dataservice.infrastructure.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@Entity
@Table(name = "transactions")
class TransactionEntity {

    @Id
    @OptIn(ExperimentalUuidApi::class)
    val id: UUID = Uuid.generateV7().toJavaUuid()

    lateinit var categoryId: UUID
    lateinit var completedDate: Instant
    lateinit var amount: BigDecimal

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var appendix: Map<String, Any>? = null

    @Version
    var version: Long = 0
}

