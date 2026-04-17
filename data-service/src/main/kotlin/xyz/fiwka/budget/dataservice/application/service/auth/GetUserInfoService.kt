package xyz.fiwka.budget.dataservice.application.service.auth

import xyz.fiwka.budget.dataservice.application.exception.type.UnauthorizedException
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.GetUserInfoCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.GetUserInfoResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.GetUserInfoUseCase
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort

class GetUserInfoService(
    private val findUserByLoginOutputPort: FindUserByLoginOutputPort
) : GetUserInfoUseCase {

    override fun execute(request: GetUserInfoCommand): GetUserInfoResponse {
        val user = findUserByLoginOutputPort.execute(request.login)
            ?: throw UnauthorizedException("User not found")

        return GetUserInfoResponse(
            id = requireNotNull(user.id),
            username = user.username,
            email = user.email
        )
    }
}

