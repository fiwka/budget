package xyz.fiwka.budget.dataservice.application.port.out.category

import xyz.fiwka.budget.port.Port
import java.util.UUID

interface DeleteCategoryByIdOutputPort : Port<UUID, Unit>

