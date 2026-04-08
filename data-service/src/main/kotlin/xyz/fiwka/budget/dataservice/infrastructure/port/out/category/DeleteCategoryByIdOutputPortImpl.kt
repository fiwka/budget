package xyz.fiwka.budget.dataservice.infrastructure.port.out.category

import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.port.out.category.DeleteCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.infrastructure.repository.CategoryRepository
import java.util.UUID

@Component
class DeleteCategoryByIdOutputPortImpl(
    private val categoryRepository: CategoryRepository
) : DeleteCategoryByIdOutputPort {
    override fun execute(request: UUID) {
        categoryRepository.deleteById(request)
    }
}

