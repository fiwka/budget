package xyz.fiwka.budget.dataservice.infrastructure.port.out.auth

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.domain.user.User
import xyz.fiwka.budget.dataservice.infrastructure.mapper.UserMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.UserRepository

@Component
class FindUserByLoginOutputPortImpl(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) : FindUserByLoginOutputPort {

    override fun execute(request: String): User? {
        val userEntity = userRepository.findByUsername(request) ?: userRepository.findByEmail(request)
        return userEntity?.let(userMapper::fromEntity)
    }
}

