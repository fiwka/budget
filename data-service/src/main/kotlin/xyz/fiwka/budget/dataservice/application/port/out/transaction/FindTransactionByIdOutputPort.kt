package xyz.fiwka.budget.dataservice.application.port.out.transaction

import xyz.fiwka.budget.dataservice.domain.transaction.Transaction
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface FindTransactionByIdOutputPort : Port<UUID, Transaction?>

