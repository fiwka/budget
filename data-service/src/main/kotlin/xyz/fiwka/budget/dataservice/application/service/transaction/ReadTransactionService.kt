package xyz.fiwka.budget.dataservice.application.service.transaction

import xyz.fiwka.budget.dataservice.application.exception.transaction.TransactionNotFoundException
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.transaction.ReadTransactionUseCase
import xyz.fiwka.budget.dataservice.application.port.out.transaction.FindTransactionByIdOutputPort

class ReadTransactionService(
    private val findTransactionByIdOutputPort: FindTransactionByIdOutputPort,
) : ReadTransactionUseCase {
    override fun execute(request: ReadTransactionCommand): ReadTransactionResponse =
        ReadTransactionResponse(
            findTransactionByIdOutputPort.execute(request.id)
                ?: throw TransactionNotFoundException(request.id)
        )
}

