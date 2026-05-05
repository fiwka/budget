package xyz.fiwka.budget.aiagent.service

import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import xyz.fiwka.budget.aiagent.model.UserInfoResponse

@Service
class UserContextService(
    webClientBuilder: WebClient.Builder,
) {

    private val webClient: WebClient = webClientBuilder.baseUrl("http://data-service").build()

    fun resolveUser(authorizationHeader: String): Mono<UserInfoResponse> = webClient.get()
        .uri("/api/user/info")
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
        .retrieve()
        .bodyToMono<UserInfoResponse>()
}
