package xyz.fiwka.budget.dataservice.application.port.out.importstatement

import xyz.fiwka.budget.dataservice.application.model.importstatement.BankStatementBank
import xyz.fiwka.budget.dataservice.application.model.importstatement.BankStatementFormat
import xyz.fiwka.budget.dataservice.application.model.importstatement.ParsedBankStatement

interface BankStatementParser {
    fun supports(bank: BankStatementBank, format: BankStatementFormat): Boolean
    fun parse(content: ByteArray): ParsedBankStatement
}
