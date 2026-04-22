package xyz.fiwka.budget.gateway.filter

import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import reactor.core.publisher.Mono
import xyz.fiwka.budget.gateway.service.AuthSessionService

@Component
class SessionBearerRelayFilter(
    private val authSessionService: AuthSessionService
) : GlobalFilter, Ordered {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        if (isPublicPath(exchange.request.path.value())) {
            return chain.filter(exchange)
        }

        return exchange.session.flatMap { session ->
            authSessionService.resolveAccessToken(session)
                .switchIfEmpty(Mono.error(UnauthorizedException()))
                .flatMap { accessToken ->
                    val request = exchange.request.mutate()
                        .headers { headers -> headers.setBearerAuth(accessToken) }
                        .build()

                    chain.filter(exchange.mutate().request(request).build())
                }
                .onErrorResume(UnauthorizedException::class.java) { unauthorized(exchange) }
        }
    }

    override fun getOrder(): Int = -10

    private fun isPublicPath(path: String): Boolean =
        path.startsWith("/api/session/") ||
            path == "/api/auth/register" ||
            path.startsWith("/actuator/") ||
            path == "/swagger-ui.html" ||
            path.startsWith("/swagger-ui/") ||
            path.startsWith("/v3/api-docs")

    private fun unauthorized(exchange: ServerWebExchange): Mono<Void> {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        return exchange.response.setComplete()
    }

    private class UnauthorizedException : RuntimeException()
}



