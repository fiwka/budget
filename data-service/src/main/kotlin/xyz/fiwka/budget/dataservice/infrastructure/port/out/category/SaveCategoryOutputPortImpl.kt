package xyz.fiwka.budget.dataservice.infrastructure.port.out.category

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.category.SaveCategoryOutputPort
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.infrastructure.mapper.CategoryMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.CategoryRepository

@Component
class SaveCategoryOutputPortImpl(
    private val categoryRepository: CategoryRepository,
    private val categoryMapper: CategoryMapper
) : SaveCategoryOutputPort {
    override fun execute(request: Category): Category =
        categoryMapper.fromEntity(
            categoryRepository.save(categoryMapper.toEntity(request))
        )
}

