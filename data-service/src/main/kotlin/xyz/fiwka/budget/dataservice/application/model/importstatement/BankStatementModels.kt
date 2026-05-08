package xyz.fiwka.budget.dataservice.application.model.importstatement

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

enum class BankStatementBank {
    VTB,
}

enum class BankStatementFormat {
    PDF,
}

data class ParsedBankStatement(
    val bank: BankStatementBank,
    val accountMask: String?,
    val periodFrom: LocalDate?,
    val periodTo: LocalDate?,
    val transactions: List<ParsedBankStatementTransaction>,
)

data class ParsedBankStatementTransaction(
    val operationDateTime: LocalDateTime,
    val processedDate: LocalDate?,
    val amount: BigDecimal,
    val currency: String,
    val commission: BigDecimal?,
    val description: String,
    val merchantName: String?,
)
