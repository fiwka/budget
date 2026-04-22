package xyz.fiwka.budget.dataservice.infrastructure.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import xyz.fiwka.budget.dataservice.infrastructure.entity.TransactionEntity
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

interface TransactionRepository : JpaRepository<TransactionEntity, UUID> {

	@Query(
		value = """
			select t.*
			from transactions t
			join categories c on c.id = t.category_id
			where c.budget_id = :budgetId
			  and (:id is null or t.id = :id)
			  and (:categoryId is null or t.category_id = :categoryId)
			  and (:completedDateFrom is null or t.completed_date >= :completedDateFrom)
			  and (:completedDateTo is null or t.completed_date <= :completedDateTo)
			  and (:amountFrom is null or t.amount >= :amountFrom)
			  and (:amountTo is null or t.amount <= :amountTo)
			  and (:appendixContains is null or cast(t.appendix as text) ilike concat('%', :appendixContains, '%'))
			order by t.completed_date desc, t.id desc
		""",
		countQuery = """
			select count(*)
			from transactions t
			join categories c on c.id = t.category_id
			where c.budget_id = :budgetId
			  and (:id is null or t.id = :id)
			  and (:categoryId is null or t.category_id = :categoryId)
			  and (:completedDateFrom is null or t.completed_date >= :completedDateFrom)
			  and (:completedDateTo is null or t.completed_date <= :completedDateTo)
			  and (:amountFrom is null or t.amount >= :amountFrom)
			  and (:amountTo is null or t.amount <= :amountTo)
			  and (:appendixContains is null or cast(t.appendix as text) ilike concat('%', :appendixContains, '%'))
		""",
		nativeQuery = true,
	)
	fun findBudgetTransactions(
		@Param("budgetId") budgetId: UUID,
		@Param("id") id: UUID?,
		@Param("categoryId") categoryId: UUID?,
		@Param("completedDateFrom") completedDateFrom: Instant?,
		@Param("completedDateTo") completedDateTo: Instant?,
		@Param("amountFrom") amountFrom: BigDecimal?,
		@Param("amountTo") amountTo: BigDecimal?,
		@Param("appendixContains") appendixContains: String?,
		pageable: Pageable,
	): Page<TransactionEntity>
}

