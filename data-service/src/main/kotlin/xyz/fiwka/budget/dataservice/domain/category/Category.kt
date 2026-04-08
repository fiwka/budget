package xyz.fiwka.budget.dataservice.domain.category

import java.util.UUID

class Category(
    val id: UUID?,
    var budgetId: UUID,
    var name: String,
    var isConsumption: Boolean
) {
    fun rename(name: String) {
        this.name = name
    }

    fun changeConsumptionType(isConsumption: Boolean) {
        this.isConsumption = isConsumption
    }
}
