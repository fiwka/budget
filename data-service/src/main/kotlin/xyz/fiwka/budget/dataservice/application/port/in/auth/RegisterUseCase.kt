package xyz.fiwka.budget.dataservice.application.port.`in`.auth

import xyz.fiwka.budget.port.Port
import java.util.UUID

interface RegisterUseCase : Port<RegisterCommand, RegisterResponse>

data class RegisterCommand(
    val username: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val id: UUID,
    val username: String,
    val email: String
)

