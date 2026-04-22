package xyz.fiwka.budget.dataservice.infrastructure.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import xyz.fiwka.budget.dataservice.infrastructure.entity.CategoryEntity
import java.util.UUID

interface CategoryRepository : JpaRepository<CategoryEntity, UUID> {

	@Query(
		value = """
			select c.*
			from categories c
			where c.budget_id = :budgetId
			  and (:id is null or c.id = :id)
			  and (:name is null or c.name ilike concat('%', cast(:name as text), '%'))
			  and (:isConsumption is null or c.is_consumption = :isConsumption)
			order by c.name, c.id
		""",
		countQuery = """
			select count(*)
			from categories c
			where c.budget_id = :budgetId
			  and (:id is null or c.id = :id)
			  and (:name is null or c.name ilike concat('%', cast(:name as text), '%'))
			  and (:isConsumption is null or c.is_consumption = :isConsumption)
		""",
		nativeQuery = true,
	)
	fun findBudgetCategories(
		@Param("budgetId") budgetId: UUID,
		@Param("id") id: UUID?,
		@Param("name") name: String?,
		@Param("isConsumption") isConsumption: Boolean?,
		pageable: Pageable,
	): Page<CategoryEntity>
}

