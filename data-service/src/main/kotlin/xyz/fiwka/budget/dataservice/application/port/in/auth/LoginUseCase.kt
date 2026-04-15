package xyz.fiwka.budget.dataservice.application.port.`in`.auth

import xyz.fiwka.budget.port.Port

interface LoginUseCase : Port<LoginCommand, LoginResponse>

data class LoginCommand(
    val login: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String,
    val tokenType: String = "Bearer"
)

