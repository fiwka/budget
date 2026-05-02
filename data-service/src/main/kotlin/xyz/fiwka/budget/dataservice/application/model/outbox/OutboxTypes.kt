package xyz.fiwka.budget.dataservice.application.model.outbox

object OutboxTypes {
    const val TRANSACTION_CREATED_EVENT = "TransactionCreatedEvent"
    const val TRANSACTION_UPDATED_EVENT = "TransactionUpdatedEvent"
    const val TRANSACTION_DELETED_EVENT = "TransactionDeletedEvent"
}

