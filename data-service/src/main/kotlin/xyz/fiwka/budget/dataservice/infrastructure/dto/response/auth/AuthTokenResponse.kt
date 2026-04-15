package xyz.fiwka.budget.dataservice.infrastructure.dto.response.auth

data class AuthTokenResponse(
    val accessToken: String,
    val tokenType: String
)

