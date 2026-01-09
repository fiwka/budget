package xyz.fiwka.budget.data.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.annotation.RestResource
import xyz.fiwka.budget.data.entity.Transaction
import java.util.*

@RepositoryRestResource(collectionResourceRel = "transactions", path = "transactions")
interface TransactionRepository : JpaRepository<Transaction, Long>, PagingAndSortingRepository<Transaction, Long> {

    @EntityGraph(attributePaths = ["budget", "category", "dataSource"])
    override fun findById(id: Long): Optional<Transaction>

    @EntityGraph(attributePaths = ["budget", "category", "dataSource"])
    override fun findAll(pageable: Pageable): Page<Transaction>

    @RestResource(path = "by-budget", rel = "by-budget")
    @EntityGraph(attributePaths = ["category", "dataSource"])
    fun findByBudgetId(budgetId: Long, pageable: Pageable): Page<Transaction>
}
