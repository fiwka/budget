package xyz.fiwka.budget.data.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import xyz.fiwka.budget.data.entity.Budget
import java.util.*

@RepositoryRestResource(collectionResourceRel = "budgets", path = "budgets")
interface BudgetRepository : JpaRepository<Budget, Long>, PagingAndSortingRepository<Budget, Long> {

    @EntityGraph(attributePaths = ["dataSources", "categories", "userRoles"])
    override fun findById(id: Long): Optional<Budget>

    @EntityGraph(attributePaths = ["dataSources", "categories", "userRoles"])
    override fun findAll(pageable: Pageable): Page<Budget>
}
