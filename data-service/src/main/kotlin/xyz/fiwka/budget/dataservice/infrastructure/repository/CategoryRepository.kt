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
			select c
			from CategoryEntity c
			where c.budgetId = :budgetId
			  and (:id is null or c.id = :id)
			  and (:name is null or lower(c.name) like lower(concat('%', :name, '%')))
			  and (:isConsumption is null or c.isConsumption = :isConsumption)
			order by c.name asc, c.id asc
		"""
	)
	fun findBudgetCategories(
		@Param("budgetId") budgetId: UUID,
		@Param("id") id: UUID?,
		@Param("name") name: String?,
		@Param("isConsumption") isConsumption: Boolean?,
		pageable: Pageable,
	): Page<CategoryEntity>
}

