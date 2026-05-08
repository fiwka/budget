package xyz.fiwka.budget.dataservice.infrastructure.dto.response.importstatement

import xyz.fiwka.budget.dataservice.application.model.importstatement.BankStatementBank
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class ImportBankStatementResponseDto(
    val bank: BankStatementBank,
    val importedCount: Int,
    val skippedCount: Int,
    val transactions: List<ImportedBankStatementTransactionDto>,
)

data class ImportedBankStatementTransactionDto(
    val transactionId: UUID,
    val categoryId: UUID,
    val completedDate: Instant,
    val amount: BigDecimal,
    val merchantName: String?,
    val description: String,
)
