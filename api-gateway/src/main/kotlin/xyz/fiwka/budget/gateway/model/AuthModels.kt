package xyz.fiwka.budget.gateway.model

data class LoginRequest(
    val login: String,
    val password: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class AuthTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String
)

data class AuthSessionTokens(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String
)

data class SessionStatusResponse(
    val authenticated: Boolean,
    val tokenType: String? = null
)

