package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.importstatement

import com.fasterxml.jackson.databind.json.JsonMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.port.`in`.importstatement.ImportBankStatementUseCase
import xyz.fiwka.budget.dataservice.application.port.out.budget.FindBudgetByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.ListBudgetCategoriesOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.category.SaveCategoryOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.importstatement.BankStatementParser
import xyz.fiwka.budget.dataservice.application.port.out.outbox.SaveOutboxMessageOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.ExistsImportedTransactionOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.SaveTransactionOutputPort
import xyz.fiwka.budget.dataservice.application.service.importstatement.ImportBankStatementService
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard

@Configuration
class BankStatementImportUseCaseConfiguration {

    @Bean
    fun importBankStatementUseCase(
        parsers: List<BankStatementParser>,
        findBudgetByIdOutputPort: FindBudgetByIdOutputPort,
        listBudgetCategoriesOutputPort: ListBudgetCategoriesOutputPort,
        saveCategoryOutputPort: SaveCategoryOutputPort,
        saveTransactionOutputPort: SaveTransactionOutputPort,
        existsImportedTransactionOutputPort: ExistsImportedTransactionOutputPort,
        saveOutboxMessageOutputPort: SaveOutboxMessageOutputPort,
        budgetAccessGuard: BudgetAccessGuard,
        jsonMapper: JsonMapper,
        @Value("\${app.kafka.topic.transaction-created:transaction-created}") transactionCreatedTopic: String,
        atomicOperationExecutor: AtomicOperationExecutor,
    ): ImportBankStatementUseCase =
        ImportBankStatementService(
            parsers = parsers,
            findBudgetByIdOutputPort = findBudgetByIdOutputPort,
            listBudgetCategoriesOutputPort = listBudgetCategoriesOutputPort,
            saveCategoryOutputPort = saveCategoryOutputPort,
            saveTransactionOutputPort = saveTransactionOutputPort,
            existsImportedTransactionOutputPort = existsImportedTransactionOutputPort,
            saveOutboxMessageOutputPort = saveOutboxMessageOutputPort,
            budgetAccessGuard = budgetAccessGuard,
            jsonMapper = jsonMapper,
            transactionCreatedTopic = transactionCreatedTopic,
            atomicOperationExecutor = atomicOperationExecutor,
        )
}
