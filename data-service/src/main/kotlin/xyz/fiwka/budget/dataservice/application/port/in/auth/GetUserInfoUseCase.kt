package xyz.fiwka.budget.dataservice.application.port.`in`.auth

import xyz.fiwka.budget.port.Port
import java.util.UUID

interface GetUserInfoUseCase : Port<GetUserInfoCommand, GetUserInfoResponse>

data class GetUserInfoCommand(
    val login: String
)

data class GetUserInfoResponse(
    val id: UUID,
    val username: String,
    val email: String
)

