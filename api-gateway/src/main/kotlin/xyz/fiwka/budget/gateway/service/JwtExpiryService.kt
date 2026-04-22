package xyz.fiwka.budget.gateway.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.Base64

@Service
class JwtExpiryService(
    private val objectMapper: ObjectMapper
) {

    fun isExpiringSoon(token: String, skew: Duration = Duration.ofSeconds(30)): Boolean {
        val expiresAt = extractExpiry(token) ?: return true
        return expiresAt.isBefore(Instant.now().plus(skew))
    }

    private fun extractExpiry(token: String): Instant? {
        val parts = token.split('.')
        if (parts.size < 2) {
            return null
        }

        return runCatching {
            val payload = String(Base64.getUrlDecoder().decode(parts[1]))
            val claims: JsonNode = objectMapper.readTree(payload)
            val exp = claims["exp"]?.asLong() ?: return null
            Instant.ofEpochSecond(exp)
        }.getOrNull()
    }
}

