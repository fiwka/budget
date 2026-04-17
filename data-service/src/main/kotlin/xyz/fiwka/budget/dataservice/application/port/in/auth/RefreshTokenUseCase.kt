package xyz.fiwka.budget.dataservice.application.port.`in`.auth

import xyz.fiwka.budget.port.Port

interface RefreshTokenUseCase : Port<RefreshTokenCommand, RefreshTokenResponse>

data class RefreshTokenCommand(
    val refreshToken: String
)

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer"
)

