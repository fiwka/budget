package xyz.fiwka.budget.dataservice.application.service.auth

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import xyz.fiwka.budget.dataservice.application.exception.type.UnauthorizedException
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.LoginCommand
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.GenerateJwtTokenOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.GenerateRefreshTokenOutputPort
import xyz.fiwka.budget.dataservice.domain.user.User
import java.util.UUID

class LoginServiceTest {

    @Test
    fun `should return jwt for valid credentials`() {
        val passwordEncoder = BCryptPasswordEncoder()
        val encodedPassword = requireNotNull(passwordEncoder.encode("Password123"))

        val findUserPort = object : FindUserByLoginOutputPort {
            override fun execute(request: String): User? =
                if (request == "alex") {
                    User(UUID.randomUUID(), "alex", "alex@example.com", encodedPassword)
                } else {
                    null
                }
        }

        val tokenPort = object : GenerateJwtTokenOutputPort {
            override fun execute(request: User): String = "jwt-token"
        }

        val refreshTokenPort = object : GenerateRefreshTokenOutputPort {
            override fun generateRefreshToken(user: User): String = "refresh-token"
        }

        val service = LoginService(findUserPort, tokenPort, refreshTokenPort, passwordEncoder)
        val response = service.execute(LoginCommand("alex", "Password123"))

        assertEquals("jwt-token", response.accessToken)
        assertEquals("refresh-token", response.refreshToken)
        assertEquals("Bearer", response.tokenType)
    }

    @Test
    fun `should throw unauthorized for invalid credentials`() {
        val passwordEncoder = BCryptPasswordEncoder()
        val findUserPort = object : FindUserByLoginOutputPort {
            override fun execute(request: String): User? = null
        }

        val tokenPort = object : GenerateJwtTokenOutputPort {
            override fun execute(request: User): String = "jwt-token"
        }

        val refreshTokenPort = object : GenerateRefreshTokenOutputPort {
            override fun generateRefreshToken(user: User): String = "refresh-token"
        }

        val service = LoginService(findUserPort, tokenPort, refreshTokenPort, passwordEncoder)

        assertThrows<UnauthorizedException> {
            service.execute(LoginCommand("alex", "Password123"))
        }
    }
}



