package xyz.fiwka.budget.dataservice.infrastructure.port.out.auth

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.auth.ExistsUserByUsernameOrEmailOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.ExistsUserByUsernameOrEmailQuery
import xyz.fiwka.budget.dataservice.infrastructure.repository.UserRepository

@Component
class ExistsUserByUsernameOrEmailOutputPortImpl(
    private val userRepository: UserRepository
) : ExistsUserByUsernameOrEmailOutputPort {

    override fun execute(request: ExistsUserByUsernameOrEmailQuery): Boolean =
        userRepository.existsByUsernameOrEmail(request.username, request.email)
}

