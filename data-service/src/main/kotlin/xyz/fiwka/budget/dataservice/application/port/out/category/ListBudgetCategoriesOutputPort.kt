package xyz.fiwka.budget.dataservice.application.port.out.category

import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface ListBudgetCategoriesOutputPort : Port<ListBudgetCategoriesRequest, PageResult<Category>>

data class ListBudgetCategoriesRequest(
    val budgetId: UUID,
    val page: Int,
    val size: Int,
    val id: UUID? = null,
    val name: String? = null,
    val isConsumption: Boolean? = null,
)

