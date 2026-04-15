package xyz.fiwka.budget.dataservice.infrastructure.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.auth.GenerateJwtTokenOutputPort
import xyz.fiwka.budget.dataservice.domain.user.User
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenService(
    @Value("\${app.security.jwt.secret}")
    private val secret: String,
    @Value("\${app.security.jwt.expiration-ms:3600000}")
    private val expirationMs: Long
) : GenerateJwtTokenOutputPort {

    override fun execute(request: User): String = generateToken(request)

    fun generateToken(user: User): String {
        val now = Instant.now()

        return Jwts.builder()
            .subject(user.username)
            .claim("uid", requireNotNull(user.id).toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMs)))
            .signWith(signingKey())
            .compact()
    }

    fun extractUsername(token: String): String? =
        runCatching {
            Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .payload
                .subject
        }.getOrNull()

    fun isTokenValid(token: String, user: User): Boolean {
        val username = extractUsername(token)
        return username == user.username
    }

    private fun signingKey(): SecretKey =
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
}

