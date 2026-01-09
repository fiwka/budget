package xyz.fiwka.budget.data.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import xyz.fiwka.budget.data.entity.BudgetUserRole
import java.util.*

@RepositoryRestResource(collectionResourceRel = "budgetUserRoles", path = "budget-user-roles")
interface BudgetUserRoleRepository : JpaRepository<BudgetUserRole, Long>, PagingAndSortingRepository<BudgetUserRole, Long> {

    @EntityGraph(attributePaths = ["budget", "user"])
    override fun findById(id: Long): Optional<BudgetUserRole>

    @EntityGraph(attributePaths = ["budget", "user"])
    override fun findAll(pageable: Pageable): Page<BudgetUserRole>

    @RestResource(path = "by-budget", rel = "by-budget")
    @EntityGraph(attributePaths = ["user"])
    fun findByBudgetId(budgetId: Long, pageable: Pageable): Page<BudgetUserRole>

    @RestResource(path = "by-user", rel = "by-user")
    @EntityGraph(attributePaths = ["budget"])
    fun findByUserId(userId: Long, pageable: Pageable): Page<BudgetUserRole>
}

