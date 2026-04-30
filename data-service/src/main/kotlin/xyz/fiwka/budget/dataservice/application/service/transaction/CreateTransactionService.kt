package xyz.fiwka.budget.dataservice.application.service.transaction

import com.fasterxml.jackson.databind.json.JsonMapper
import xyz.fiwka.budget.application.operation.AtomicOperationExecutor
import xyz.fiwka.budget.dataservice.application.exception.category.CategoryNotFoundException
import xyz.fiwka.budget.dataservice.application.model.outbox.OutboxTypes
import xyz.fiwka.budget.dataservice.application.model.outbox.TransactionCreatedOutboxPayload
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.CreateTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.CreateTransactionResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.CreateTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.out.category.FindCategoryByIdOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.outbox.SaveOutboxMessageOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.transaction.SaveTransactionOutputPort
import xyz.fiwka.budget.dataservice.application.service.security.BudgetAccessGuard
import xyz.fiwka.budget.dataservice.domain.budget.BudgetPermission
import xyz.fiwka.budget.dataservice.domain.outbox.OutboxMessage
import xyz.fiwka.budget.dataservice.domain.transaction.Transaction

class CreateTransactionService(
    private val findCategoryByIdOutputPort: FindCategoryByIdOutputPort,
    private val saveTransactionOutputPort: SaveTransactionOutputPort,
    private val saveOutboxMessageOutputPort: SaveOutboxMessageOutputPort,
    private val budgetAccessGuard: BudgetAccessGuard,
    private val jsonMapper: JsonMapper,
    private val transactionCreatedTopic: String,
    private val atomicOperationExecutor: AtomicOperationExecutor,
) : CreateTransactionUseCase {

    override fun execute(request: CreateTransactionCommand): CreateTransactionResponse =
        atomicOperationExecutor.execute {
            val category = findCategoryByIdOutputPort.execute(request.categoryId)
                ?: throw CategoryNotFoundException(request.categoryId)

            budgetAccessGuard.requireBudgetPermission(request.actorLogin, category.budgetId, BudgetPermission.EDIT)

            val transaction = saveTransactionOutputPort.execute(
                Transaction(
                    id = null,
                    categoryId = request.categoryId,
                    completedDate = request.completedDate,
                    amount = request.amount,
                    appendix = request.appendix,
                )
            )

            saveOutboxMessageOutputPort.execute(
                OutboxMessage(
                    id = null,
                    type = OutboxTypes.TRANSACTION_CREATED_EVENT,
                    topic = transactionCreatedTopic,
                    payload = jsonMapper.valueToTree(TransactionCreatedOutboxPayload.fromTransaction(transaction)),
                )
            )

            CreateTransactionResponse(transaction)
        }
}

