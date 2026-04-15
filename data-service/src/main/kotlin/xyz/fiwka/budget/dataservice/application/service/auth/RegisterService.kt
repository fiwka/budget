package xyz.fiwka.budget.dataservice.application.service.auth

import org.springframework.security.crypto.password.PasswordEncoder
import xyz.fiwka.budget.dataservice.application.exception.type.ConflictException
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RegisterCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RegisterResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RegisterUseCase
import xyz.fiwka.budget.dataservice.application.port.out.auth.ExistsUserByUsernameOrEmailOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.ExistsUserByUsernameOrEmailQuery
import xyz.fiwka.budget.dataservice.application.port.out.auth.SaveUserOutputPort
import xyz.fiwka.budget.dataservice.domain.user.User

class RegisterService(
    private val existsUserByUsernameOrEmailOutputPort: ExistsUserByUsernameOrEmailOutputPort,
    private val saveUserOutputPort: SaveUserOutputPort,
    private val passwordEncoder: PasswordEncoder
) : RegisterUseCase {

    override fun execute(request: RegisterCommand): RegisterResponse {
        if (
            existsUserByUsernameOrEmailOutputPort.execute(
                ExistsUserByUsernameOrEmailQuery(request.username, request.email)
            )
        ) {
            throw ConflictException("User with the same username or email already exists")
        }

        val savedUser = saveUserOutputPort.execute(
            User(
                id = null,
                username = request.username,
                email = request.email,
                passwordHash = requireNotNull(passwordEncoder.encode(request.password))
            )
        )

        return RegisterResponse(
            id = requireNotNull(savedUser.id),
            username = savedUser.username,
            email = savedUser.email
        )
    }
}


