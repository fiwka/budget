package xyz.fiwka.budget.analytics.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xyz.fiwka.budget.analytics.infrastructure.entity.ProcessedEventEntity

@Repository
interface ProcessedEventRepository : JpaRepository<ProcessedEventEntity, String>
