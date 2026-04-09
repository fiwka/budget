package xyz.fiwka.budget.dataservice.infrastructure.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import xyz.fiwka.budget.dataservice.infrastructure.entity.OutboxEntity
import java.util.UUID

interface OutboxRepository : JpaRepository<OutboxEntity, UUID> {
    fun findAllByOrderByCreatedAtAsc(pageable: Pageable): List<OutboxEntity>
}

