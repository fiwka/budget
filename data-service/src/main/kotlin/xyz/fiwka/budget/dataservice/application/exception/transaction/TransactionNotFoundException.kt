package xyz.fiwka.budget.dataservice.application.exception.transaction

import xyz.fiwka.budget.dataservice.application.exception.type.NotFoundException
import java.util.UUID

class TransactionNotFoundException(id: UUID) : NotFoundException("Transaction with $id not found")