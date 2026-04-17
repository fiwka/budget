package xyz.fiwka.budget.dataservice.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import xyz.fiwka.budget.dataservice.infrastructure.entity.BudgetRoleEntity
import java.util.UUID

interface BudgetRoleRepository : JpaRepository<BudgetRoleEntity, UUID> {

    @Query(
        value = """
            select r.key
            from budget_roles br
            join roles r on br.role_id = r.id
            where br.user_id = :userId and br.budget_id = :budgetId
            limit 1
        """,
        nativeQuery = true,
    )
    fun findRoleKeyByUserIdAndBudgetId(
        @Param("userId") userId: UUID,
        @Param("budgetId") budgetId: UUID,
    ): Int?
}

