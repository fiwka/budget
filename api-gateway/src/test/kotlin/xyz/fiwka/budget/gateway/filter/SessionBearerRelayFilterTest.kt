package xyz.fiwka.budget.gateway.filter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import xyz.fiwka.budget.gateway.service.AuthSessionService

class SessionBearerRelayFilterTest {

    @Test
    fun `should skip public session paths`() {
        val service = mock(AuthSessionService::class.java)
        val filter = SessionBearerRelayFilter(service)
        var captured: ServerWebExchange? = null
        val chain = GatewayFilterChain { exchange ->
            captured = exchange
            Mono.empty()
        }
        val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/session/status"))

        filter.filter(exchange, chain).block()

        assertEquals(exchange, captured)
        assertNull(exchange.response.statusCode)
    }

    @Test
    fun `should add bearer token to private request`() {
        val service = mock(AuthSessionService::class.java)
        val filter = SessionBearerRelayFilter(service)
        `when`(service.resolveAccessToken(anyValue())).thenReturn(Mono.just("access-token"))
        var captured: ServerWebExchange? = null
        val chain = GatewayFilterChain { exchange ->
            captured = exchange
            Mono.empty()
        }
        val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/budget/accessible"))

        filter.filter(exchange, chain).block()

        assertEquals("Bearer access-token", captured?.request?.headers?.getFirst(HttpHeaders.AUTHORIZATION))
        assertEquals(-10, filter.order)
    }

    @Test
    fun `should return unauthorized when session has no token`() {
        val service = mock(AuthSessionService::class.java)
        val filter = SessionBearerRelayFilter(service)
        `when`(service.resolveAccessToken(anyValue())).thenReturn(Mono.empty())
        val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/budget/accessible"))

        filter.filter(exchange) { Mono.empty() }.block()

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.response.statusCode)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyValue(): T = any<T>() ?: null as T
}
