package xyz.fiwka.budget.gateway.service

import org.springframework.stereotype.Service
import org.springframework.web.server.WebSession
import reactor.core.publisher.Mono
import xyz.fiwka.budget.gateway.client.DataServiceAuthClient
import xyz.fiwka.budget.gateway.model.AuthSessionTokens
import xyz.fiwka.budget.gateway.model.AuthTokenResponse
import xyz.fiwka.budget.gateway.model.LoginRequest
import xyz.fiwka.budget.gateway.model.RefreshTokenRequest

@Service
class AuthSessionService(
    private val dataServiceAuthClient: DataServiceAuthClient,
    private val jwtExpiryService: JwtExpiryService
) {

    fun login(request: LoginRequest, session: WebSession): Mono<AuthTokenResponse> = dataServiceAuthClient
        .login(request)
        .doOnNext { saveTokens(session, it) }

    fun refresh(session: WebSession): Mono<AuthTokenResponse> {
        val tokens = readTokens(session) ?: return Mono.empty()

        return dataServiceAuthClient
            .refresh(RefreshTokenRequest(tokens.refreshToken))
            .doOnNext { saveTokens(session, it) }
    }

    fun resolveAccessToken(session: WebSession): Mono<String> {
        val tokens = readTokens(session) ?: return Mono.empty()

        if (!jwtExpiryService.isExpiringSoon(tokens.accessToken)) {
            return Mono.just(tokens.accessToken)
        }

        return refresh(session)
            .map { it.accessToken }
            .switchIfEmpty(session.invalidate().then(Mono.empty()))
            .onErrorResume { session.invalidate().then(Mono.empty()) }
    }

    fun isAuthenticated(session: WebSession): Boolean = readTokens(session) != null

    fun logout(session: WebSession): Mono<Void> = session.invalidate()

    private fun saveTokens(session: WebSession, response: AuthTokenResponse) {
        session.attributes[SESSION_TOKENS_ATTR] = AuthSessionTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            tokenType = response.tokenType
        )
    }

    private fun readTokens(session: WebSession): AuthSessionTokens? =
        session.attributes[SESSION_TOKENS_ATTR] as? AuthSessionTokens

    companion object {
        private const val SESSION_TOKENS_ATTR = "AUTH_SESSION_TOKENS"
    }
}

