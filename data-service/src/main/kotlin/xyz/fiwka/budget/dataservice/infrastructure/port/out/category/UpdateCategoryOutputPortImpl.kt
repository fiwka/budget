package xyz.fiwka.budget.dataservice.infrastructure.port.out.category

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.exception.category.CategoryNotFoundException
import xyz.fiwka.budget.dataservice.application.port.out.category.UpdateCategoryOutputPort
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.infrastructure.mapper.CategoryMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.CategoryRepository

@Component
class UpdateCategoryOutputPortImpl(
    private val categoryRepository: CategoryRepository,
    private val categoryMapper: CategoryMapper
) : UpdateCategoryOutputPort {
    override fun execute(request: Category): Category {
        val id = requireNotNull(request.id)
        val categoryEntity = categoryRepository.findById(id)
            .orElseThrow { CategoryNotFoundException(id) }

        categoryEntity.budgetId = request.budgetId
        categoryEntity.name = request.name
        categoryEntity.isConsumption = request.isConsumption

        return categoryMapper.fromEntity(categoryRepository.save(categoryEntity))
    }
}

