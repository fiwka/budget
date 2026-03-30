package xyz.fiwka.budget.dataservice.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import xyz.fiwka.budget.dataservice.infrastructure.entity.BudgetEntity
import java.util.UUID

interface BudgetRepository : JpaRepository<BudgetEntity, UUID>