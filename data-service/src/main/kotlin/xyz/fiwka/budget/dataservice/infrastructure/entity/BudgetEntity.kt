package xyz.fiwka.budget.dataservice.infrastructure.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@Entity
@Table(name = "budgets")
class BudgetEntity {

    @Id
    @OptIn(ExperimentalUuidApi::class)
    val id: UUID = Uuid.generateV7().toJavaUuid()
    lateinit var name: String
    lateinit var description: String
}