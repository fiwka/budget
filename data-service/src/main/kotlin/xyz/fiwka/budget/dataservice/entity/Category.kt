package xyz.fiwka.budget.dataservice.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@Entity
@Table(name = "categories")
class Category {

    @Id
    @OptIn(ExperimentalUuidApi::class)
    var id: UUID = Uuid.generateV7().toJavaUuid()
    @ManyToOne
    @JoinColumn(name = "budget_id")
    lateinit var budget: Budget
    var name: String = ""
}