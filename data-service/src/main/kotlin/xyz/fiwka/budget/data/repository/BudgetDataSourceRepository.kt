package xyz.fiwka.budget.data.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import xyz.fiwka.budget.data.entity.BudgetDataSource
import java.util.*

@RepositoryRestResource(collectionResourceRel = "budgetDataSources", path = "budget-data-sources")
interface BudgetDataSourceRepository : JpaRepository<BudgetDataSource, Long>, PagingAndSortingRepository<BudgetDataSource, Long> {

    @EntityGraph(attributePaths = ["budget", "dataSource"])
    override fun findById(id: Long): Optional<BudgetDataSource>

    @EntityGraph(attributePaths = ["budget", "dataSource"])
    override fun findAll(pageable: Pageable): Page<BudgetDataSource>

    @RestResource(path = "by-budget", rel = "by-budget")
    @EntityGraph(attributePaths = ["dataSource"])
    fun findByBudgetId(budgetId: Long, pageable: Pageable): Page<BudgetDataSource>

    @RestResource(path = "by-data-source", rel = "by-data-source")
    @EntityGraph(attributePaths = ["budget"])
    fun findByDataSourceId(dataSourceId: Long, pageable: Pageable): Page<BudgetDataSource>
}

