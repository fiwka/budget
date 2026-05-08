package xyz.fiwka.budget.dataservice.infrastructure.bankstatement

import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Component
import xyz.fiwka.budget.dataservice.application.exception.type.BadRequestException
import xyz.fiwka.budget.dataservice.application.model.importstatement.BankStatementBank
import xyz.fiwka.budget.dataservice.application.model.importstatement.BankStatementFormat
import xyz.fiwka.budget.dataservice.application.model.importstatement.ParsedBankStatement
import xyz.fiwka.budget.dataservice.application.model.importstatement.ParsedBankStatementTransaction
import xyz.fiwka.budget.dataservice.application.port.out.importstatement.BankStatementParser
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.nio.charset.Charset
import kotlin.text.Charsets.UTF_8

@Component
class VtbPdfBankStatementParser : BankStatementParser {

    override fun supports(bank: BankStatementBank, format: BankStatementFormat): Boolean =
        bank == BankStatementBank.VTB && format == BankStatementFormat.PDF

    override fun parse(content: ByteArray): ParsedBankStatement {
        val text = Loader.loadPDF(content).use { document ->
            PDFTextStripper().getText(document)
        }
        val lines = text.lineSequence()
            .map(::repairMojibake)
            .map(String::trim)
            .filter(String::isNotEmpty)
            .toList()

        return ParsedBankStatement(
            bank = BankStatementBank.VTB,
            accountMask = extractAccountMask(lines),
            periodFrom = extractStatementPeriod(lines)?.first,
            periodTo = extractStatementPeriod(lines)?.second,
            transactions = parseTransactions(lines),
        )
    }

    private fun parseTransactions(lines: List<String>): List<ParsedBankStatementTransaction> {
        val rowBasedTransactions = parseRowBasedTransactions(lines)
        if (rowBasedTransactions.isNotEmpty()) {
            return rowBasedTransactions
        }

        return parseColumnBasedTransactions(lines)
    }

    private fun parseRowBasedTransactions(lines: List<String>): List<ParsedBankStatementTransaction> {
        val transactions = mutableListOf<ParsedBankStatementTransaction>()
        var index = 0

        while (index + 1 < lines.size) {
            val operationDate = parseDateOrNull(lines[index])
            val match = ROW_TRANSACTION_REGEX.matchEntire(lines[index + 1])
            if (operationDate != null && match != null) {
                val description = match.groupValues[8].trim()
                transactions += ParsedBankStatementTransaction(
                    operationDateTime = LocalDateTime.of(operationDate, parseTime(match.groupValues[1])),
                    processedDate = parseDate(match.groupValues[2]),
                    amount = parseAmount(match.groupValues[3]),
                    currency = match.groupValues[4],
                    commission = parseAmount(match.groupValues[6]),
                    description = description,
                    merchantName = extractMerchantName(description),
                )
                index += 2
                continue
            }

            index++
        }

        return transactions
    }

    private fun parseColumnBasedTransactions(lines: List<String>): List<ParsedBankStatementTransaction> {
        val transactions = mutableListOf<ParsedBankStatementTransaction>()
        var index = 0

        while (index < lines.size) {
            if (!isTableHeaderStart(lines, index)) {
                index++
                continue
            }

            index = skipTableHeader(lines, index)
            val operationDateTimes = mutableListOf<LocalDateTime>()
            while (index + 1 < lines.size && isDate(lines[index]) && isTime(lines[index + 1])) {
                operationDateTimes += LocalDateTime.of(parseDate(lines[index]), parseTime(lines[index + 1]))
                index += 2
            }

            var operationIndex = 0
            while (index < lines.size && !isTableHeaderStart(lines, index)) {
                if (isPageFooterOrTail(lines[index])) {
                    index++
                    continue
                }

                if (isDate(lines[index]) && (index + 1 >= lines.size || !isTime(lines[index + 1]))) {
                    val operationDateTime = operationDateTimes.getOrNull(operationIndex)
                    val result = parseTransactionDetails(lines, index, operationDateTime)
                    if (result != null) {
                        transactions += result.transaction
                        operationIndex++
                        index = result.nextIndex
                        continue
                    }
                }

                index++
            }
        }

        return transactions
    }

    private fun parseTransactionDetails(
        lines: List<String>,
        startIndex: Int,
        operationDateTime: LocalDateTime?,
    ): ParsedTransactionResult? {
        val processedDate = parseDateOrNull(lines[startIndex]) ?: return null
        var index = startIndex + 1

        val operationMoney = readMoneyWithCurrency(lines, index) ?: return null
        index = operationMoney.nextIndex

        val cardAmount = readAmount(lines, index) ?: return null
        index = cardAmount.nextIndex

        val commissionAndDescription = readCommissionAndDescription(lines, index) ?: return null
        index = commissionAndDescription.nextIndex

        val completedAt = operationDateTime ?: processedDate.atStartOfDay()
        val description = commissionAndDescription.description

        return ParsedTransactionResult(
            transaction = ParsedBankStatementTransaction(
                operationDateTime = completedAt,
                processedDate = processedDate,
                amount = operationMoney.amount,
                currency = operationMoney.currency,
                commission = commissionAndDescription.commission,
                description = description,
                merchantName = extractMerchantName(description),
            ),
            nextIndex = index,
        )
    }

    private fun readMoneyWithCurrency(lines: List<String>, index: Int): MoneyResult? {
        val line = lines.getOrNull(index) ?: return null
        MONEY_WITH_CURRENCY_REGEX.matchEntire(line)?.let { match ->
            return MoneyResult(
                amount = parseAmount(match.groupValues[1]),
                currency = match.groupValues[2],
                nextIndex = index + 1,
            )
        }

        val nextLine = lines.getOrNull(index + 1) ?: return null
        if (AMOUNT_REGEX.matches(line) && CURRENCY_REGEX.matches(nextLine)) {
            return MoneyResult(
                amount = parseAmount(line),
                currency = nextLine,
                nextIndex = index + 2,
            )
        }

        return null
    }

    private fun readAmount(lines: List<String>, index: Int): AmountResult? {
        val line = lines.getOrNull(index) ?: return null
        if (AMOUNT_REGEX.matches(line)) {
            return AmountResult(nextIndex = index + 1)
        }

        MONEY_WITH_CURRENCY_REGEX.matchEntire(line)?.let {
            return AmountResult(nextIndex = index + 1)
        }

        return null
    }

    private fun readCommissionAndDescription(lines: List<String>, index: Int): CommissionDescriptionResult? {
        val line = lines.getOrNull(index) ?: return null
        COMMISSION_DESCRIPTION_REGEX.matchEntire(line)?.let { match ->
            return CommissionDescriptionResult(
                commission = parseAmount(match.groupValues[1]),
                description = collectDescription(lines, index + 1, match.groupValues[3]),
                nextIndex = findDescriptionEnd(lines, index + 1),
            )
        }

        MONEY_WITH_CURRENCY_REGEX.matchEntire(line)?.let { match ->
            val descriptionStart = index + 1
            val description = lines.getOrNull(descriptionStart) ?: return null
            return CommissionDescriptionResult(
                commission = parseAmount(match.groupValues[1]),
                description = collectDescription(lines, descriptionStart + 1, description),
                nextIndex = findDescriptionEnd(lines, descriptionStart + 1),
            )
        }

        return null
    }

    private fun collectDescription(lines: List<String>, startIndex: Int, firstLine: String): String {
        val parts = mutableListOf(firstLine)
        var index = startIndex
        while (index < lines.size && !isDate(lines[index]) && !isTableHeaderStart(lines, index) && !isPageFooterOrTail(lines[index])) {
            parts += lines[index]
            index++
        }
        return parts.joinToString(" ").trim()
    }

    private fun findDescriptionEnd(lines: List<String>, startIndex: Int): Int {
        var index = startIndex
        while (index < lines.size && !isDate(lines[index]) && !isTableHeaderStart(lines, index) && !isPageFooterOrTail(lines[index])) {
            index++
        }
        return index
    }

    private fun extractAccountMask(lines: List<String>): String? {
        lines.forEach { line ->
            CARD_MASK_REGEX.find(line)?.let { return it.groupValues[1] }
        }
        return null
    }

    private fun extractStatementPeriod(lines: List<String>): Pair<LocalDate, LocalDate>? {
        lines.forEach { line ->
            STATEMENT_PERIOD_REGEX.find(line)?.let {
                return parseDate(it.groupValues[1]) to parseDate(it.groupValues[2])
            }
        }
        return null
    }

    private fun extractMerchantName(description: String): String? =
        description
            .removePrefix("Оплата товаров и услуг.")
            .removePrefix("Оплата товаров и услуг")
            .trim()
            .ifBlank { null }

    private fun skipTableHeader(lines: List<String>, startIndex: Int): Int {
        var index = startIndex
        while (index < lines.size && !lines[index].contains("Описание операции")) {
            index++
        }
        if (index >= lines.size) {
            throw BadRequestException("VTB statement table header is incomplete")
        }
        return index + 1
    }

    private fun isTableHeaderStart(lines: List<String>, index: Int): Boolean =
        lines.getOrNull(index) == "Дата и время" && lines.getOrNull(index + 1) == "операции"

    private fun isPageFooterOrTail(line: String): Boolean =
        line.matches(PAGE_NUMBER_REGEX) || line.startsWith("Спасибо, что Вы с нами")

    private fun isDate(line: String): Boolean = DATE_REGEX.matches(line)

    private fun isTime(line: String): Boolean = TIME_REGEX.matches(line)

    private fun parseDate(line: String): LocalDate =
        LocalDate.parse(line, DATE_FORMATTER)

    private fun parseDateOrNull(line: String): LocalDate? =
        runCatching { parseDate(line) }.getOrNull()

    private fun parseTime(line: String): LocalTime =
        LocalTime.parse(line, TIME_FORMATTER)

    private fun parseAmount(value: String): BigDecimal =
        value.replace(',', '.').toBigDecimal()

    private fun repairMojibake(line: String): String =
        if (line.contains('Р') || line.contains('С')) {
            runCatching { String(line.toByteArray(WINDOWS_1251), UTF_8) }.getOrDefault(line)
        } else {
            line
        }

    private data class ParsedTransactionResult(
        val transaction: ParsedBankStatementTransaction,
        val nextIndex: Int,
    )

    private data class MoneyResult(
        val amount: BigDecimal,
        val currency: String,
        val nextIndex: Int,
    )

    private data class AmountResult(
        val nextIndex: Int,
    )

    private data class CommissionDescriptionResult(
        val commission: BigDecimal,
        val description: String,
        val nextIndex: Int,
    )

    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss")
        private val DATE_REGEX = Regex("""\d{2}\.\d{2}\.\d{4}""")
        private val TIME_REGEX = Regex("""\d{2}:\d{2}:\d{2}""")
        private val PAGE_NUMBER_REGEX = Regex("""\d+""")
        private val AMOUNT_REGEX = Regex("""[+-]?\d+(?:[.,]\d+)?""")
        private val CURRENCY_REGEX = Regex("""[A-Z]{3}""")
        private val MONEY_WITH_CURRENCY_REGEX = Regex("""([+-]?\d+(?:[.,]\d+)?)\s+([A-Z]{3})""")
        private val COMMISSION_DESCRIPTION_REGEX = Regex("""([+-]?\d+(?:[.,]\d+)?)\s+([A-Z]{3})\s+(.+)""")
        private val ROW_TRANSACTION_REGEX = Regex(
            """(\d{2}:\d{2}:\d{2})\s+(\d{2}\.\d{2}\.\d{4})\s+([+-]?\d+(?:[.,]\d+)?)\s+([A-Z]{3})\s+([+-]?\d+(?:[.,]\d+)?)\s+([+-]?\d+(?:[.,]\d+)?)\s+([A-Z]{3})\s+(.+)"""
        )
        private val CARD_MASK_REGEX = Regex("""(\d{6}\*+\d{4})""")
        private val STATEMENT_PERIOD_REGEX = Regex("""(\d{2}\.\d{2}\.\d{4})\s+-\s+(\d{2}\.\d{2}\.\d{4})""")
        private val WINDOWS_1251: Charset = Charset.forName("windows-1251")
    }
}
