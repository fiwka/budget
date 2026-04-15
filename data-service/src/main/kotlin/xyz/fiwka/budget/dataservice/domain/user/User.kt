package xyz.fiwka.budget.dataservice.domain.user

import java.util.UUID

class User(
    val id: UUID?,
    val username: String,
    val email: String,
    val passwordHash: String
)

