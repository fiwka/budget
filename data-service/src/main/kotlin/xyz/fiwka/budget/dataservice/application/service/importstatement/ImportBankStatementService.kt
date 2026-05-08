package xyz.fiwka.budget.dataservice.application.service.importstatement

import com.fasterxml.jackson.databind.json.JsonMapper
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.budget.BudgetNotFoundException
import xyz.fiwka.budget.dataservice.application.exception.type.BadRequestException
import xyz.fiwka.budget.dataservice.application.model.importstatement.ParsedBankStatement
import xyz.fiwka.budget.dataservice.application.model.importstatement.ParsedBankStatementTransaction
import xyz.fiwka.budget.dataservice.application.model.outbox.OutboxTypes
import xyz.fiwka.budget.dataservice.application.model.outbox.TransactionEventOutboxPayload
import xyz.fiwka.budget.dataservice.application.port.`in`.importstatement.ImportBankStatementCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.importstatement.ImportBankStatementResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.importstatement.ImportBankStatementUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.importstatement.ImportedBankStatementTransaction
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.ListBudgetCategoriesOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.ListBudgetCategoriesRequest
import xyz.fiwka.budget.dataservice.application.port.out.category.SaveCategoryOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.importstatement.BankStatementParser
import xyz.fiwka.budget.dataservice.application.port.out.outbox.SaveOutboxMessageOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.ExistsImportedTransactionOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.ExistsImportedTransactionRequest
import xyz.fiwka.budget.dataservice.application.port.out.transaction.SaveTransactionOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetPermission
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.domain.outbox.OutboxMessage
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import java.security.MessageDigest
import java.util.*

class ImportBankStatementService(
    private val parsers: List<BankStatementParser>,
    private val findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
    private val listBudgetCategoriesOutputPort: ListBudgetCategoriesOutputPort,
    private val saveCategoryOutputPort: SaveCategoryOutputPort,
    private val saveTransactionOutputPort: SaveTransactionOutputPort,
    private val existsImportedTransactionOutputPort: ExistsImportedTransactionOutputPort,
    private val saveOutboxMessageOutputPort: SaveOutboxMessageOutputPort,
    private val budgetAccessGuard: BudgetAccessGuard,
    private val jsonMapper: JsonMapper,
    private val transactionCreatedTopic: String,
    private val atomicOperationExecutor: AtomicOperationExecutor,
) : ImportBankStatementUseCase {

    override fun execute(request: ImportBankStatementCommand): ImportBankStatementResponse =
        atomicOperationExecutor.execute {
            budgetAccessGuard.requireBudgetPermission(request.actorLogin, request.budgetId, BudgetPermission.EDIT)
            val budget = findBudgetByIdOutputPort.execute(request.budgetId)
                ?: throw BudgetNotFoundException(request.budgetId)
            val parser = parsers.firstOrNull { it.supports(request.bank, request.format) }
                ?: throw BadRequestException("Unsupported bank statement source: ${request.bank}/${request.format}")

            val statement = parser.parse(request.content)
            if (statement.transactions.isEmpty()) {
                throw BadRequestException("Bank statement does not contain transactions")
            }

            val categories = listBudgetCategoriesOutputPort.execute(
                ListBudgetCategoriesRequest(
                    budgetId = request.budgetId,
                    page = 0,
                    size = 1000,
                )
            ).items.associateByTo(mutableMapOf()) { categoryCacheKey(it.name, it.isConsumption) }
            var skippedCount = 0
            val importedTransactions = statement.transactions.mapNotNull { parsedTransaction ->
                val importFingerprint = importFingerprint(statement, parsedTransaction)
                val alreadyImported = existsImportedTransactionOutputPort.execute(
                    ExistsImportedTransactionRequest(
                        budgetId = request.budgetId,
                        importFingerprint = importFingerprint,
                    )
                )
                if (alreadyImported) {
                    skippedCount++
                    return@mapNotNull null
                }

                val isConsumption = parsedTransaction.amount.signum() < 0
                val categoryName = categoryName(parsedTransaction)
                val category = categories.getOrPut(categoryCacheKey(categoryName, isConsumption)) {
                    findCategoryByName(
                        budgetId = request.budgetId,
                        name = categoryName,
                        isConsumption = isConsumption,
                    ) ?: saveCategoryOutputPort.execute(
                        budget.createCategory(
                            name = categoryName,
                            isConsumption = isConsumption,
                        )
                    )
                }

                val completedDate = parsedTransaction.operationDateTime.atZone(request.zoneId).toInstant()
                val transaction = saveTransactionOutputPort.execute(
                    Transaction(
                        id = null,
                        categoryId = requireNotNull(category.id),
                        completedDate = completedDate,
                        amount = parsedTransaction.amount.abs(),
                        appendix = statementAppendix(request, statement, parsedTransaction, importFingerprint),
                    )
                )

                saveOutboxMessageOutputPort.execute(
                    OutboxMessage(
                        id = null,
                        type = OutboxTypes.TRANSACTION_CREATED_EVENT,
                        topic = transactionCreatedTopic,
                        payload = jsonMapper.valueToTree(
                            TransactionEventOutboxPayload.fromTransaction(
                                transaction = transaction,
                                budgetId = request.budgetId,
                                isConsumption = category.isConsumption,
                            )
                        ),
                    )
                )

                ImportedBankStatementTransaction(
                    transactionId = requireNotNull(transaction.id),
                    categoryId = transaction.categoryId,
                    completedDate = transaction.completedDate,
                    amount = transaction.amount,
                    merchantName = parsedTransaction.merchantName,
                    description = parsedTransaction.description,
                )
            }

            ImportBankStatementResponse(
                bank = request.bank,
                importedCount = importedTransactions.size,
                skippedCount = skippedCount,
                transactions = importedTransactions,
            )
        }

    private fun categoryName(transaction: ParsedBankStatementTransaction): String =
        transaction.merchantName
            ?.trim()
            ?.takeIf(String::isNotEmpty)
            ?: transaction.description.trim().ifEmpty { DEFAULT_CATEGORY_NAME }

    private fun categoryCacheKey(name: String, isConsumption: Boolean): String =
        "${isConsumption}:${name.trim().lowercase()}"

    private fun findCategoryByName(
        budgetId: UUID,
        name: String,
        isConsumption: Boolean,
    ): Category? =
        listBudgetCategoriesOutputPort.execute(
            ListBudgetCategoriesRequest(
                budgetId = budgetId,
                page = 0,
                size = 1,
                name = name,
                isConsumption = isConsumption,
            )
        ).items.firstOrNull { it.name.equals(name, ignoreCase = true) && it.isConsumption == isConsumption }

    private fun statementAppendix(
        request: ImportBankStatementCommand,
        statement: ParsedBankStatement,
        transaction: ParsedBankStatementTransaction,
        importFingerprint: String,
    ): Map<String, Any> =
        linkedMapOf<String, Any?>(
            "source" to "${request.bank.name}-${request.fileName ?: UNKNOWN_FILE_NAME}",
            "importFingerprint" to importFingerprint,
            "bank" to request.bank.name,
            "format" to request.format.name,
            "fileName" to request.fileName,
            "accountMask" to statement.accountMask,
            "statementPeriodFrom" to statement.periodFrom?.toString(),
            "statementPeriodTo" to statement.periodTo?.toString(),
            "operationDateTime" to transaction.operationDateTime.toString(),
            "processedDate" to transaction.processedDate?.toString(),
            "operationAmount" to transaction.amount.toPlainString(),
            "currency" to transaction.currency,
            "commission" to transaction.commission?.toPlainString(),
            "merchantName" to transaction.merchantName,
            "description" to transaction.description,
        ).mapNotNull { (key, value) -> value?.let { key to it } }
            .toMap()

    private fun importFingerprint(
        statement: ParsedBankStatement,
        transaction: ParsedBankStatementTransaction,
    ): String {
        val value = listOf(
            statement.bank.name,
            statement.accountMask.orEmpty(),
            transaction.operationDateTime.toString(),
            transaction.processedDate?.toString().orEmpty(),
            transaction.amount.toPlainString(),
            transaction.currency,
            transaction.merchantName.orEmpty(),
            transaction.description,
        ).joinToString("|")

        val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val DEFAULT_CATEGORY_NAME = "Без категории"
        private const val UNKNOWN_FILE_NAME = "unknown"
    }
}
