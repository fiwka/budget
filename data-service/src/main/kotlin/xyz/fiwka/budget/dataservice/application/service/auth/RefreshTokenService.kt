package xyz.fiwka.budget.dataservice.application.service.auth

import xyz.fiwka.budget.dataservice.application.exception.type.UnauthorizedException
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RefreshTokenCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RefreshTokenResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RefreshTokenUseCase
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.GenerateJwtTokenOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.GenerateRefreshTokenOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.ReadRefreshTokenOutputPort

class RefreshTokenService(
    private val readRefreshTokenOutputPort: ReadRefreshTokenOutputPort,
    private val findUserByLoginOutputPort: FindUserByLoginOutputPort,
    private val generateJwtTokenOutputPort: GenerateJwtTokenOutputPort,
    private val generateRefreshTokenOutputPort: GenerateRefreshTokenOutputPort
) : RefreshTokenUseCase {

    override fun execute(request: RefreshTokenCommand): RefreshTokenResponse {
        val username = readRefreshTokenOutputPort.extractUsernameFromRefreshToken(request.refreshToken)
            ?: throw UnauthorizedException("Invalid refresh token")

        val user = findUserByLoginOutputPort.execute(username)
            ?: throw UnauthorizedException("Invalid refresh token")

        if (!readRefreshTokenOutputPort.isRefreshTokenValid(request.refreshToken, user)) {
            throw UnauthorizedException("Invalid refresh token")
        }

        return RefreshTokenResponse(
            accessToken = generateJwtTokenOutputPort.execute(user),
            refreshToken = generateRefreshTokenOutputPort.generateRefreshToken(user)
        )
    }
}

