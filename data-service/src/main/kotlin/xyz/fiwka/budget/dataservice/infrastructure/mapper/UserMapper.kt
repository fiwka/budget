package xyz.fiwka.budget.dataservice.infrastructure.mapper

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.domain.user.User
import xyz.fiwka.budget.dataservice.infrastructure.entity.UserEntity
import java.util.UUID

@Component
class UserMapper {

    fun toEntity(user: User): UserEntity =
        UserEntity().apply {
            id = user.id ?: UUID.randomUUID()
            username = user.username
            email = user.email
            passwordHash = user.passwordHash
        }

    fun fromEntity(userEntity: UserEntity): User =
        User(
            id = userEntity.id,
            username = userEntity.username,
            email = userEntity.email,
            passwordHash = userEntity.passwordHash
        )
}

