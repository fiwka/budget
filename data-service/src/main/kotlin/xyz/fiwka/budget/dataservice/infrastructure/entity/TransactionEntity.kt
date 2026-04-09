package xyz.fiwka.budget.dataservice.infrastructure.entity

import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
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
    var appendix: JsonNode? = null

    @Version
    var version: Long = 0
}

