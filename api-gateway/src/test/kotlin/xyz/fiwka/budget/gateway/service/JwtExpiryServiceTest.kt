package xyz.fiwka.budget.gateway.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.Base64

class JwtExpiryServiceTest {

    private val service = JwtExpiryService(ObjectMapper())

    @Test
    fun `should treat malformed token as expiring`() {
        assertTrue(service.isExpiringSoon("not-a-jwt"))
    }

    @Test
    fun `should detect token expiring inside skew`() {
        val token = tokenWithExpiry(Instant.now().plusSeconds(10))

        assertTrue(service.isExpiringSoon(token, Duration.ofSeconds(30)))
    }

    @Test
    fun `should keep token with expiry outside skew`() {
        val token = tokenWithExpiry(Instant.now().plusSeconds(120))

        assertFalse(service.isExpiringSoon(token, Duration.ofSeconds(30)))
    }

    private fun tokenWithExpiry(expiresAt: Instant): String {
        val payload = """{"exp":${expiresAt.epochSecond}}"""
        val encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.toByteArray())
        return "header.$encodedPayload.signature"
    }
}
