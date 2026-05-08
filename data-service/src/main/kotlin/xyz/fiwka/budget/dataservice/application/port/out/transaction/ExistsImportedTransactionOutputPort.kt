package xyz.fiwka.budget.dataservice.application.port.out.transaction

import xyz.fiwka.budget.port.Port
import java.util.UUID

interface ExistsImportedTransactionOutputPort : Port<ExistsImportedTransactionRequest, Boolean>

data class ExistsImportedTransactionRequest(
    val budgetId: UUID,
    val importFingerprint: String,
)
