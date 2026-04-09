package xyz.fiwka.budget.dataservice.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import xyz.fiwka.budget.dataservice.infrastructure.entity.TransactionEntity
import java.util.UUID

interface TransactionRepository : JpaRepository<TransactionEntity, UUID>

