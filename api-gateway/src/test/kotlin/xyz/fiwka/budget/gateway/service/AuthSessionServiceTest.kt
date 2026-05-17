package xyz.fiwka.budget.gateway.service

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.mock.web.server.MockWebSession
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import xyz.fiwka.budget.gateway.client.DataServiceAuthClient
import xyz.fiwka.budget.gateway.model.AuthTokenResponse
import xyz.fiwka.budget.gateway.model.LoginRequest
import xyz.fiwka.budget.gateway.model.RefreshTokenRequest

class AuthSessionServiceTest {

    private val client = mock(DataServiceAuthClient::class.java)
    private val jwtExpiryService = mock(JwtExpiryService::class.java)
    private val service = AuthSessionService(client, jwtExpiryService)

    @Test
    fun `login should save tokens in session`() {
        val session = MockWebSession()
        val request = LoginRequest("alex", "password")
        `when`(client.login(request)).thenReturn(Mono.just(tokens("access", "refresh")))

        StepVerifier.create(service.login(request, session))
            .expectNext(tokens("access", "refresh"))
            .verifyComplete()

        assertTrue(service.isAuthenticated(session))
    }

    @Test
    fun `ensureAuthenticated should return existing tokens when access token is fresh`() {
        val session = MockWebSession()
        `when`(client.login(LoginRequest("alex", "password"))).thenReturn(Mono.just(tokens("access", "refresh")))
        service.login(LoginRequest("alex", "password"), session).block()
        `when`(jwtExpiryService.isExpiringSoon("access")).thenReturn(false)

        StepVerifier.create(service.ensureAuthenticated(session))
            .expectNextMatches { it.accessToken == "access" && it.refreshToken == "refresh" }
            .verifyComplete()
    }

    @Test
    fun `ensureAuthenticated should refresh expiring access token`() {
        val session = MockWebSession()
        `when`(client.login(LoginRequest("alex", "password"))).thenReturn(Mono.just(tokens("old-access", "old-refresh")))
        service.login(LoginRequest("alex", "password"), session).block()
        `when`(jwtExpiryService.isExpiringSoon("old-access")).thenReturn(true)
        `when`(client.refresh(RefreshTokenRequest("old-refresh"))).thenReturn(Mono.just(tokens("new-access", "new-refresh")))

        StepVerifier.create(service.resolveAccessToken(session))
            .expectNext("new-access")
            .verifyComplete()
    }

    @Test
    fun `ensureAuthenticated should invalidate session when refresh fails`() {
        val session = MockWebSession()
        `when`(client.login(LoginRequest("alex", "password"))).thenReturn(Mono.just(tokens("old-access", "old-refresh")))
        service.login(LoginRequest("alex", "password"), session).block()
        `when`(jwtExpiryService.isExpiringSoon("old-access")).thenReturn(true)
        `when`(client.refresh(RefreshTokenRequest("old-refresh"))).thenReturn(Mono.error(IllegalStateException("boom")))

        StepVerifier.create(service.ensureAuthenticated(session))
            .verifyComplete()

        assertFalse(service.isAuthenticated(session))
    }

    private fun tokens(accessToken: String, refreshToken: String) =
        AuthTokenResponse(accessToken = accessToken, refreshToken = refreshToken, tokenType = "Bearer")
}
