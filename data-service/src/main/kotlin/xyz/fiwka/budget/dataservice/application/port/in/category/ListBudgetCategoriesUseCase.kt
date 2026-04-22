package xyz.fiwka.budget.dataservice.application.port.`in`.category

import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface ListBudgetCategoriesUseCase : Port<ListBudgetCategoriesCommand, ListBudgetCategoriesResponse>

data class ListBudgetCategoriesCommand(
    val budgetId: UUID,
    val actorLogin: String,
    val page: Int,
    val size: Int,
    val id: UUID? = null,
    val name: String? = null,
    val isConsumption: Boolean? = null,
)

@JvmInline
value class ListBudgetCategoriesResponse(val categories: PageResult<Category>)

