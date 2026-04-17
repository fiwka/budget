package xyz.fiwka.budget.dataservice.infrastructure.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Claims
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.auth.GenerateJwtTokenOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.GenerateRefreshTokenOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.ReadRefreshTokenOutputPort
import xyz.fiwka.budget.dataservice.domain.user.User
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenService(
    @Value("\${app.security.jwt.secret}")
    private val secret: String,
    @Value("\${app.security.jwt.access-expiration-ms:300000}")
    private val accessExpirationMs: Long,
    @Value("\${app.security.jwt.refresh-expiration-ms:1800000}")
    private val refreshExpirationMs: Long
) : GenerateJwtTokenOutputPort, GenerateRefreshTokenOutputPort, ReadRefreshTokenOutputPort {

    override fun execute(request: User): String = generateToken(request)

    override fun generateRefreshToken(user: User): String = generateToken(user, refreshExpirationMs, "refresh")

    fun generateToken(user: User): String = generateToken(user, accessExpirationMs, "access")

    private fun generateToken(user: User, expirationMs: Long, tokenType: String): String {
        val now = Instant.now()

        return Jwts.builder()
            .subject(user.username)
            .claim("uid", requireNotNull(user.id).toString())
            .claim("typ", tokenType)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMs)))
            .signWith(signingKey())
            .compact()
    }

    fun extractUsername(token: String): String? = parseClaims(token)?.subject

    override fun extractUsernameFromRefreshToken(token: String): String? {
        val claims = parseClaims(token) ?: return null
        return if (claims["typ"] == "refresh") claims.subject else null
    }

    fun isTokenValid(token: String, user: User): Boolean {
        val claims = parseClaims(token) ?: return false
        return claims.subject == user.username && claims["typ"] == "access"
    }

    override fun isRefreshTokenValid(token: String, user: User): Boolean {
        val claims = parseClaims(token) ?: return false
        return claims.subject == user.username && claims["typ"] == "refresh"
    }

    private fun parseClaims(token: String): Claims? =
        runCatching {
            Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .payload
        }.getOrNull()


    private fun signingKey(): SecretKey =
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
}

