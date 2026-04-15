package xyz.fiwka.budget.dataservice.infrastructure.dto.response.auth

import java.util.UUID

data class RegisterResponse(
    val id: UUID,
    val username: String,
    val email: String
)

