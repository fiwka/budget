package xyz.fiwka.budget.dataservice.domain.transaction

import tools.jackson.databind.JsonNode
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class Transaction(
    val id: UUID?,
    var categoryId: UUID,
    var completedDate: Instant,
    var amount: BigDecimal,
    var appendix: Map<String, Any>? = null,
) {
    fun update(
        categoryId: UUID,
        completedDate: Instant,
        amount: BigDecimal,
        appendix: Map<String, Any>?,
    ) {
        this.categoryId = categoryId
        this.completedDate = completedDate
        this.amount = amount
        this.appendix = appendix
    }
}

