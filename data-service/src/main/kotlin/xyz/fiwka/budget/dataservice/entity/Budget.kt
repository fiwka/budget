package xyz.fiwka.budget.dataservice.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@Entity
@Table(name = "budgets")
class Budget {

    @Id
    @OptIn(ExperimentalUuidApi::class)
    var id: UUID = Uuid.generateV7().toJavaUuid()
}