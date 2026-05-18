package xyz.fiwka.budget.dataservice.infrastructure.controller.auth

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.TestingAuthenticationToken
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.GetUserInfoCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.GetUserInfoResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.GetUserInfoUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.LoginCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.LoginResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.LoginUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RefreshTokenCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RefreshTokenResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RefreshTokenUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RegisterCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RegisterResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RegisterUseCase
import xyz.fiwka.budget.dataservice.infrastructure.controller.user.UserController
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.auth.LoginRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.auth.RefreshTokenRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.auth.RegisterRequest
import java.util.UUID

class AuthAndUserControllerTest {

    @Test
    fun `should register user`() {
        val userId = UUID.randomUUID()
        var command: RegisterCommand? = null
        val controller = AuthController(
            registerUseCase = object : RegisterUseCase {
                override fun execute(request: RegisterCommand): RegisterResponse {
                    command = request
                    return RegisterResponse(userId, request.username, request.email)
                }
            },
            loginUseCase = loginUseCase(),
            refreshTokenUseCase = refreshTokenUseCase(),
        )

        val response = controller.register(RegisterRequest("alex", "alex@example.com", "password1"))

        assertEquals(RegisterCommand("alex", "alex@example.com", "password1"), command)
        assertEquals(userId, response.id)
        assertEquals("alex", response.username)
    }

    @Test
    fun `should login user`() {
        var command: LoginCommand? = null
        val controller = AuthController(
            registerUseCase = registerUseCase(),
            loginUseCase = object : LoginUseCase {
                override fun execute(request: LoginCommand): LoginResponse {
                    command = request
                    return LoginResponse("access", "refresh")
                }
            },
            refreshTokenUseCase = refreshTokenUseCase(),
        )

        val response = controller.login(LoginRequest("alex", "password1"))

        assertEquals(LoginCommand("alex", "password1"), command)
        assertEquals("access", response.accessToken)
        assertEquals("refresh", response.refreshToken)
        assertEquals("Bearer", response.tokenType)
    }

    @Test
    fun `should refresh token`() {
        var command: RefreshTokenCommand? = null
        val controller = AuthController(
            registerUseCase = registerUseCase(),
            loginUseCase = loginUseCase(),
            refreshTokenUseCase = object : RefreshTokenUseCase {
                override fun execute(request: RefreshTokenCommand): RefreshTokenResponse {
                    command = request
                    return RefreshTokenResponse("new-access", "new-refresh")
                }
            },
        )

        val response = controller.refresh(RefreshTokenRequest("old-refresh"))

        assertEquals(RefreshTokenCommand("old-refresh"), command)
        assertEquals("new-access", response.accessToken)
        assertEquals("new-refresh", response.refreshToken)
    }

    @Test
    fun `should return current user info`() {
        val userId = UUID.randomUUID()
        var command: GetUserInfoCommand? = null
        val controller = UserController(
            getUserInfoUseCase = object : GetUserInfoUseCase {
                override fun execute(request: GetUserInfoCommand): GetUserInfoResponse {
                    command = request
                    return GetUserInfoResponse(userId, "alex", "alex@example.com")
                }
            }
        )

        val response = controller.info(TestingAuthenticationToken("alex", "credentials"))

        assertEquals(GetUserInfoCommand("alex"), command)
        assertEquals(userId, response.id)
        assertEquals("alex@example.com", response.email)
    }

    private fun registerUseCase(): RegisterUseCase =
        object : RegisterUseCase {
            override fun execute(request: RegisterCommand): RegisterResponse =
                RegisterResponse(UUID.randomUUID(), request.username, request.email)
        }

    private fun loginUseCase(): LoginUseCase =
        object : LoginUseCase {
            override fun execute(request: LoginCommand): LoginResponse =
                LoginResponse("access", "refresh")
        }

    private fun refreshTokenUseCase(): RefreshTokenUseCase =
        object : RefreshTokenUseCase {
            override fun execute(request: RefreshTokenCommand): RefreshTokenResponse =
                RefreshTokenResponse("access", "refresh")
        }
}
