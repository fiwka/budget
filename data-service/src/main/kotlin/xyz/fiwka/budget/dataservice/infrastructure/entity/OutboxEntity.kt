package xyz.fiwka.budget.dataservice.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@Entity
@Table(name = "outbox")
class OutboxEntity {

    @Id
    @OptIn(ExperimentalUuidApi::class)
    var id: UUID = Uuid.generateV7().toJavaUuid()
    var type: String = ""
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    var payload: String = ""
    @Column(name = "created_at")
    var createdAt: ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
}