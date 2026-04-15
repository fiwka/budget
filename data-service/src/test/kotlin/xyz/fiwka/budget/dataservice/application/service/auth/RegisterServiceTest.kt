package xyz.fiwka.budget.dataservice.application.service.auth

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import xyz.fiwka.budget.dataservice.application.exception.type.ConflictException
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RegisterCommand
import xyz.fiwka.budget.dataservice.application.port.out.auth.ExistsUserByUsernameOrEmailOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.ExistsUserByUsernameOrEmailQuery
import xyz.fiwka.budget.dataservice.application.port.out.auth.SaveUserOutputPort
import xyz.fiwka.budget.dataservice.domain.user.User
import java.util.UUID

class RegisterServiceTest {

    @Test
    fun `should register new user`() {
        val passwordEncoder = BCryptPasswordEncoder()

        val existsPort = object : ExistsUserByUsernameOrEmailOutputPort {
            override fun execute(request: ExistsUserByUsernameOrEmailQuery): Boolean = false
        }

        var savedUser: User? = null
        val savePort = object : SaveUserOutputPort {
            override fun execute(request: User): User {
                savedUser = request
                return User(UUID.randomUUID(), request.username, request.email, request.passwordHash)
            }
        }

        val service = RegisterService(existsPort, savePort, passwordEncoder)
        val response = service.execute(RegisterCommand("alex", "alex@example.com", "Password123"))

        assertEquals("alex", response.username)
        assertEquals("alex@example.com", response.email)
        assertNotEquals("Password123", savedUser?.passwordHash)
    }

    @Test
    fun `should throw conflict when user already exists`() {
        val existsPort = object : ExistsUserByUsernameOrEmailOutputPort {
            override fun execute(request: ExistsUserByUsernameOrEmailQuery): Boolean = true
        }

        val savePort = object : SaveUserOutputPort {
            override fun execute(request: User): User = request
        }

        val service = RegisterService(existsPort, savePort, BCryptPasswordEncoder())

        assertThrows<ConflictException> {
            service.execute(RegisterCommand("alex", "alex@example.com", "Password123"))
        }
    }
}

