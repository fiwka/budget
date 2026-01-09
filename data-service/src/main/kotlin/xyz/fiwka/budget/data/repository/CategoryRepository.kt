package xyz.fiwka.budget.data.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import xyz.fiwka.budget.data.entity.Category
import java.util.*

@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
interface CategoryRepository : JpaRepository<Category, Long>, PagingAndSortingRepository<Category, Long> {

    @EntityGraph(attributePaths = ["budget"])
    override fun findById(id: Long): Optional<Category>

    @EntityGraph(attributePaths = ["budget"])
    override fun findAll(pageable: Pageable): Page<Category>

    @RestResource(path = "by-budget", rel = "by-budget")
    @EntityGraph(attributePaths = ["budget"])
    fun findByBudgetId(budgetId: Long, pageable: Pageable): Page<Category>
}
