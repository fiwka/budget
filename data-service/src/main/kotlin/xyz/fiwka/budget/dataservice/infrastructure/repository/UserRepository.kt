package xyz.fiwka.budget.dataservice.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import xyz.fiwka.budget.dataservice.infrastructure.entity.UserEntity
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun existsByUsernameOrEmail(username: String, email: String): Boolean
    fun findByUsername(username: String): UserEntity?
    fun findByEmail(email: String): UserEntity?
}

