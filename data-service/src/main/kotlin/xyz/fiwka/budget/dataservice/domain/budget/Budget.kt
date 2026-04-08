package xyz.fiwka.budget.dataservice.domain.budget

import xyz.fiwka.budget.dataservice.application.exception.category.CategoryBelongsToAnotherBudgetException
import xyz.fiwka.budget.dataservice.domain.category.Category
import java.util.UUID

class Budget(
    val id: UUID?,
    var name: String,
    var description: String
) {
    fun owns(category: Category): Boolean =
        id != null && category.budgetId == id

    fun createCategory(name: String, isConsumption: Boolean): Category =
        Category(
            id = null,
            budgetId = requireNotNull(id),
            name = name,
            isConsumption = isConsumption
        )

    fun updateCategory(category: Category, name: String, isConsumption: Boolean): Category {
        runCatching {
            require(owns(category))

            category.rename(name)
            category.changeConsumptionType(isConsumption)

            return category
        }.getOrElse {
            throw CategoryBelongsToAnotherBudgetException(requireNotNull(category.id), requireNotNull(id))
        }
    }
}
