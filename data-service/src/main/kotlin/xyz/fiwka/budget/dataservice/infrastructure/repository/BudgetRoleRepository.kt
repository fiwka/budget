package xyz.fiwka.budget.dataservice.infrastructure.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import xyz.fiwka.budget.dataservice.infrastructure.entity.BudgetRoleEntity
import java.util.UUID

interface AccessibleBudgetProjection {
    fun getBudgetId(): UUID
    fun getBudgetName(): String
    fun getBudgetDescription(): String
    fun getRoleKey(): Int
}

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

    @Query(
        value = """
            select
                b.id as budgetId,
                b.name as budgetName,
                b.description as budgetDescription,
                r.key as roleKey
            from budget_roles br
            join budgets b on b.id = br.budget_id
            join roles r on r.id = br.role_id
            where br.user_id = :userId
              and (:budgetId is null or b.id = :budgetId)
              and (:name is null or lower(b.name) like lower(concat('%', :name, '%')))
              and (:description is null or lower(b.description) like lower(concat('%', :description, '%')))
              and (:roleKey is null or r.key = :roleKey)
            order by b.name asc, b.id asc
        """,
        countQuery = """
            select count(*)
            from budget_roles br
            join budgets b on b.id = br.budget_id
            join roles r on r.id = br.role_id
            where br.user_id = :userId
              and (:budgetId is null or b.id = :budgetId)
              and (:name is null or lower(b.name) like lower(concat('%', :name, '%')))
              and (:description is null or lower(b.description) like lower(concat('%', :description, '%')))
              and (:roleKey is null or r.key = :roleKey)
        """,
        nativeQuery = true,
    )
    fun findAccessibleBudgets(
        @Param("userId") userId: UUID,
        @Param("budgetId") budgetId: UUID?,
        @Param("name") name: String?,
        @Param("description") description: String?,
        @Param("roleKey") roleKey: Int?,
        pageable: Pageable,
    ): Page<AccessibleBudgetProjection>
}

