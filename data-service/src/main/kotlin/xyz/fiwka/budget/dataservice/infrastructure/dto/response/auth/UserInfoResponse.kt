package xyz.fiwka.budget.dataservice.infrastructure.dto.response.auth

import java.util.UUID

data class UserInfoResponse(
    val id: UUID,
    val username: String,
    val email: String
)

