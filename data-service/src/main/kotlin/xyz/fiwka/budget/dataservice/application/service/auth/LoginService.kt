package xyz.fiwka.budget.dataservice.application.service.auth

import org.springframework.security.crypto.password.PasswordEncoder
import xyz.fiwka.budget.dataservice.application.exception.type.UnauthorizedException
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.LoginCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.LoginResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.LoginUseCase
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.GenerateJwtTokenOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.GenerateRefreshTokenOutputPort

class LoginService(
    private val findUserByLoginOutputPort: FindUserByLoginOutputPort,
    private val generateJwtTokenOutputPort: GenerateJwtTokenOutputPort,
    private val generateRefreshTokenOutputPort: GenerateRefreshTokenOutputPort,
    private val passwordEncoder: PasswordEncoder
) : LoginUseCase {

    override fun execute(request: LoginCommand): LoginResponse {
        val user = findUserByLoginOutputPort.execute(request.login)
            ?: throw UnauthorizedException("Invalid login or password")

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw UnauthorizedException("Invalid login or password")
        }

        return LoginResponse(
            accessToken = generateJwtTokenOutputPort.execute(user),
            refreshToken = generateRefreshTokenOutputPort.generateRefreshToken(user)
        )
    }
}

