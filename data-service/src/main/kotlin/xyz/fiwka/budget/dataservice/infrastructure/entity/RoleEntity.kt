package xyz.fiwka.budget.dataservice.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "roles")
class RoleEntity {

    @Id
    var id: UUID = UUID.randomUUID()

    @Column(name = "key", nullable = false, unique = true)
    var roleKey: Int = 0

    @Column(name = "can_view", nullable = false)
    var canView: Boolean = false

    @Column(name = "can_edit", nullable = false)
    var canEdit: Boolean = false

    @Column(name = "can_manage", nullable = false)
    var canManage: Boolean = false
}

