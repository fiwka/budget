package xyz.fiwka.budget.gateway.controller

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import xyz.fiwka.budget.gateway.model.LoginRequest
import xyz.fiwka.budget.gateway.model.SessionStatusResponse
import xyz.fiwka.budget.gateway.service.AuthSessionService

@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins = ["http://localhost:5173"], maxAge = 3600, allowCredentials = "true")
class AuthSessionController(
    private val authSessionService: AuthSessionService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, exchange: ServerWebExchange): Mono<SessionStatusResponse> = exchange
        .session
        .flatMap { session ->
            authSessionService.login(request, session)
                .map { SessionStatusResponse(authenticated = true, tokenType = it.tokenType) }
        }

    @PostMapping("/refresh")
    fun refresh(exchange: ServerWebExchange): Mono<SessionStatusResponse> = exchange
        .session
        .flatMap { session ->
            authSessionService.refresh(session)
                .map { SessionStatusResponse(authenticated = true, tokenType = it.tokenType) }
                .defaultIfEmpty(SessionStatusResponse(authenticated = false))
        }

    @PostMapping("/logout")
    fun logout(exchange: ServerWebExchange): Mono<Void> = exchange
        .session
        .flatMap(authSessionService::logout)

    @GetMapping("/status")
    fun status(exchange: ServerWebExchange): Mono<SessionStatusResponse> = exchange
        .session
        .map { session ->
            SessionStatusResponse(authenticated = authSessionService.isAuthenticated(session))
        }
}

