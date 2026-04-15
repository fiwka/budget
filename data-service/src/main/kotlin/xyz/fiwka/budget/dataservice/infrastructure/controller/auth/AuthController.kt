package xyz.fiwka.budget.dataservice.infrastructure.controller.auth

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.LoginCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.LoginUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RegisterCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RegisterUseCase
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.auth.LoginRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.auth.RegisterRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.auth.AuthTokenResponse
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.auth.RegisterResponse

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequest): RegisterResponse {
        val response = registerUseCase.execute(RegisterCommand(request.username, request.email, request.password))
        return RegisterResponse(response.id, response.username, response.email)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthTokenResponse {
        val response = loginUseCase.execute(LoginCommand(request.login, request.password))
        return AuthTokenResponse(response.accessToken, response.tokenType)
    }
}

