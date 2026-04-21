package xyz.fiwka.budget.dataservice.infrastructure.mapper

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.category.CategoryResponse
import xyz.fiwka.budget.dataservice.infrastructure.entity.CategoryEntity

@Component
class CategoryMapper {

    fun toEntity(category: Category): CategoryEntity {
        val entity = CategoryEntity()

        entity.budgetId = category.budgetId
        entity.name = category.name
        entity.isConsumption = category.isConsumption

        return entity
    }

    fun fromEntity(categoryEntity: CategoryEntity): Category =
        Category(
            id = categoryEntity.id,
            budgetId = categoryEntity.budgetId,
            name = categoryEntity.name,
            isConsumption = categoryEntity.isConsumption
        )

    fun toDto(category: Category): CategoryResponse =
        CategoryResponse(
            id = requireNotNull(category.id),
            budgetId = category.budgetId,
            name = category.name,
            isConsumption = category.isConsumption
        )
}

