package xyz.fiwka.budget.dataservice.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity {

    @Id
    var id: UUID = UUID.randomUUID()

    @Column(nullable = false, unique = true)
    lateinit var username: String

    @Column(nullable = false, unique = true)
    lateinit var email: String

    @Column(name = "password_hash", nullable = false)
    lateinit var passwordHash: String
}

