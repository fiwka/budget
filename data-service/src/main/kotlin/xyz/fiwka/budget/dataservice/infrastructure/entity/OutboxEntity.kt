package xyz.fiwka.budget.dataservice.infrastructure.entity

import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "outbox")
class OutboxEntity {

    @Id
    @OptIn(ExperimentalUuidApi::class)
    val id: UUID = Uuid.generateV7().toJavaUuid()

    lateinit var type: String
    lateinit var topic: String

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    lateinit var payload: JsonNode

    var createdAt: Instant = Instant.now()
}

