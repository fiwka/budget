package xyz.fiwka.budget.dataservice.infrastructure.port.out.auth

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.auth.SaveUserOutputPort
import xyz.fiwka.budget.dataservice.domain.user.User
import xyz.fiwka.budget.dataservice.infrastructure.mapper.UserMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.UserRepository

@Component
class SaveUserOutputPortImpl(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) : SaveUserOutputPort {

    override fun execute(request: User): User =
        userMapper.fromEntity(
            userRepository.save(userMapper.toEntity(request))
        )
}

