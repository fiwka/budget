package xyz.fiwka.budget.dataservice.application.port.out.category

import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.port.Port

interface UpdateCategoryOutputPort : Port<Category, Category>

