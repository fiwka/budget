package xyz.fiwka.budget.dataservice.application.port.`in`.importstatement

import xyz.fiwka.budget.dataservice.application.model.importstatement.BankStatementBank
import xyz.fiwka.budget.dataservice.application.model.importstatement.BankStatementFormat
import xyz.fiwka.budget.port.Port
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

interface ImportBankStatementUseCase : Port<ImportBankStatementCommand, ImportBankStatementResponse>

data class ImportBankStatementCommand(
    val budgetId: UUID,
    val bank: BankStatementBank,
    val format: BankStatementFormat,
    val fileName: String?,
    val content: ByteArray,
    val actorLogin: String,
    val zoneId: ZoneId = ZoneId.systemDefault(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImportBankStatementCommand

        if (budgetId != other.budgetId) return false
        if (bank != other.bank) return false
        if (format != other.format) return false
        if (fileName != other.fileName) return false
        if (!content.contentEquals(other.content)) return false
        if (actorLogin != other.actorLogin) return false
        if (zoneId != other.zoneId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = budgetId.hashCode()
        result = 31 * result + bank.hashCode()
        result = 31 * result + format.hashCode()
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + content.contentHashCode()
        result = 31 * result + actorLogin.hashCode()
        result = 31 * result + zoneId.hashCode()
        return result
    }
}

data class ImportBankStatementResponse(
    val bank: BankStatementBank,
    val importedCount: Int,
    val skippedCount: Int,
    val transactions: List<ImportedBankStatementTransaction>,
)

data class ImportedBankStatementTransaction(
    val transactionId: UUID,
    val categoryId: UUID,
    val completedDate: Instant,
    val amount: BigDecimal,
    val merchantName: String?,
    val description: String,
)
