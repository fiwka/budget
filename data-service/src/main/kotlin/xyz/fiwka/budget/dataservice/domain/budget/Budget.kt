package xyz.fiwka.budget.dataservice.domain.budget

import java.util.UUID

class Budget(
    val id: UUID?,
    var name: String,
    var description: String
)