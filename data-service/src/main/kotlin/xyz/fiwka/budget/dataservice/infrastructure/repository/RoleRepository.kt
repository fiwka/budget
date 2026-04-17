package xyz.fiwka.budget.dataservice.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import xyz.fiwka.budget.dataservice.infrastructure.entity.RoleEntity
import java.util.UUID

interface RoleRepository : JpaRepository<RoleEntity, UUID> {
    fun findByRoleKey(roleKey: Int): RoleEntity?
}

