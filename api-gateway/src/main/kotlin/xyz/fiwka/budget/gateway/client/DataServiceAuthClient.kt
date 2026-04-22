package xyz.fiwka.budget.gateway.client

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import xyz.fiwka.budget.gateway.model.AuthTokenResponse
import xyz.fiwka.budget.gateway.model.LoginRequest
import xyz.fiwka.budget.gateway.model.RefreshTokenRequest

@Component
class DataServiceAuthClient(
    webClientBuilder: WebClient.Builder
) {

    private val webClient: WebClient = webClientBuilder
        .baseUrl("http://data-service")
        .build()

    fun login(request: LoginRequest): Mono<AuthTokenResponse> = webClient
        .post()
        .uri("/api/auth/login")
        .bodyValue(request)
        .retrieve()
        .bodyToMono(AuthTokenResponse::class.java)

    fun refresh(request: RefreshTokenRequest): Mono<AuthTokenResponse> = webClient
        .post()
        .uri("/api/auth/refresh")
        .bodyValue(request)
        .retrieve()
        .bodyToMono(AuthTokenResponse::class.java)
}

