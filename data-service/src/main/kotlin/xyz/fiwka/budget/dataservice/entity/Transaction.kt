package xyz.fiwka.budget.dataservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@Entity
@Table(name = "transactions")
class Transaction {

    @Id
    @OptIn(ExperimentalUuidApi::class)
    var id: UUID = Uuid.generateV7().toJavaUuid()
    @ManyToOne
    @JoinColumn(name = "category_id")
    lateinit var category: Category
    @Column(name = "completed_date")
    var completedDate: ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
    var amount: BigDecimal = BigDecimal.ZERO
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "appendix", columnDefinition = "jsonb")
    var appendix: String? = null
    var version: Long = 0L
    @Column(name = "created_at")
    var createdAt: ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
    @Column(name = "updated_at")
    var updatedAt: ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
}