package xyz.fiwka.budget.dataservice.application.port.out.category

import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface FindCategoryByIdOutputPort : Port<UUID, Category?>

