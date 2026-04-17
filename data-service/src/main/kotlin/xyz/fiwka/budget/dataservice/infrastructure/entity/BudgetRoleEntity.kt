package xyz.fiwka.budget.dataservice.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "budget_roles")
class BudgetRoleEntity {

    @Id
    var id: UUID = UUID.randomUUID()

    @Column(name = "budget_id", nullable = false)
    lateinit var budgetId: UUID

    @Column(name = "user_id", nullable = false)
    lateinit var userId: UUID

    @Column(name = "role_id", nullable = false)
    lateinit var roleId: UUID
}

