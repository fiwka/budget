package xyz.fiwka.budget.dataservice.infrastructure.port.out.category

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.infrastructure.mapper.CategoryMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.CategoryRepository
import java.util.UUID

@Component
class FindCategoryByIdOutputPortImpl(
    private val categoryRepository: CategoryRepository,
    private val categoryMapper: CategoryMapper
) : FindCategoryByIdOutputPort {
    override fun execute(request: UUID): Category? =
        categoryRepository.findById(request)
            .map(categoryMapper::fromEntity)
            .orElse(null)
}

