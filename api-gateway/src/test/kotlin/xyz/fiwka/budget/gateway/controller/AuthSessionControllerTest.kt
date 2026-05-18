package xyz.fiwka.budget.gateway.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.server.WebSession
import reactor.core.publisher.Mono
import xyz.fiwka.budget.gateway.model.AuthSessionTokens
import xyz.fiwka.budget.gateway.model.AuthTokenResponse
import xyz.fiwka.budget.gateway.model.LoginRequest
import xyz.fiwka.budget.gateway.service.AuthSessionService

class AuthSessionControllerTest {

    @Test
    fun `should login and return authenticated status`() {
        val service = mock(AuthSessionService::class.java)
        val controller = AuthSessionController(service)
        val exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/session/login"))
        val request = LoginRequest("alex", "password")
        `when`(service.login(anyValue(), anyValue()))
            .thenReturn(Mono.just(AuthTokenResponse("access", "refresh", "Bearer")))

        val response = requireNotNull(controller.login(request, exchange).block())

        assertEquals(true, response.authenticated)
        assertEquals("Bearer", response.tokenType)
        verify(service).login(anyValue(), anyValue())
    }

    @Test
    fun `should refresh authenticated and unauthenticated sessions`() {
        val service = mock(AuthSessionService::class.java)
        val controller = AuthSessionController(service)
        val exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/session/refresh"))
        `when`(service.refresh(anyValue()))
            .thenReturn(Mono.just(AuthTokenResponse("access", "refresh", "Bearer")))

        val authenticated = requireNotNull(controller.refresh(exchange).block())

        `when`(service.refresh(anyValue())).thenReturn(Mono.empty())
        val unauthenticated = requireNotNull(controller.refresh(exchange).block())

        assertEquals(true, authenticated.authenticated)
        assertEquals("Bearer", authenticated.tokenType)
        assertEquals(false, unauthenticated.authenticated)
    }

    @Test
    fun `should logout`() {
        val service = mock(AuthSessionService::class.java)
        val controller = AuthSessionController(service)
        val exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/session/logout"))
        `when`(service.logout(anyValue())).thenReturn(Mono.empty())

        controller.logout(exchange).block()

        verify(service).logout(anyValue())
    }

    @Test
    fun `should return status`() {
        val service = mock(AuthSessionService::class.java)
        val controller = AuthSessionController(service)
        val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/session/status"))
        `when`(service.ensureAuthenticated(anyValue()))
            .thenReturn(Mono.just(AuthSessionTokens("access", "refresh", "Bearer")))

        val authenticated = requireNotNull(controller.status(exchange).block())

        `when`(service.ensureAuthenticated(anyValue())).thenReturn(Mono.empty())
        val unauthenticated = requireNotNull(controller.status(exchange).block())

        assertEquals(true, authenticated.authenticated)
        assertEquals("Bearer", authenticated.tokenType)
        assertEquals(false, unauthenticated.authenticated)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyValue(): T = any<T>() ?: null as T
}
