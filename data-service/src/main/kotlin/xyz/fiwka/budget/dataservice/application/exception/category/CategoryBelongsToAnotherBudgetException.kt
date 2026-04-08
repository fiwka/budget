package xyz.fiwka.budget.dataservice.application.exception.category

import xyz.fiwka.budget.dataservice.application.exception.type.BadRequestException
import java.util.UUID

class CategoryBelongsToAnotherBudgetException(
    categoryId: UUID,
    budgetId: UUID
) : BadRequestException("Category with $categoryId does not belong to budget with $budgetId")

