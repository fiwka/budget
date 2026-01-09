package xyz.fiwka.budget.data.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id
    val id: Long? = null,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null
)