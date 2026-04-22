package xyz.fiwka.budget.dataservice.infrastructure.port.out.category

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.application.port.out.category.ListBudgetCategoriesOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.ListBudgetCategoriesRequest
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.infrastructure.mapper.CategoryMapper
import xyz.fiwka.budget.dataservice.infrastructure.repository.CategoryRepository

@Component
class ListBudgetCategoriesOutputPortImpl(
    private val categoryRepository: CategoryRepository,
    private val categoryMapper: CategoryMapper,
) : ListBudgetCategoriesOutputPort {

    override fun execute(request: ListBudgetCategoriesRequest): PageResult<Category> {
        val page = categoryRepository.findBudgetCategories(
            budgetId = request.budgetId,
            id = request.id,
            name = request.name,
            isConsumption = request.isConsumption,
            pageable = PageRequest.of(request.page, request.size),
        )

        return PageResult(
            items = page.content.map(categoryMapper::fromEntity),
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
        )
    }
}

