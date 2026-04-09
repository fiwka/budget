package xyz.fiwka.budget.dataservice.application.port.out.transaction

import xyz.fiwka.budget.port.Port
import java.util.UUID

interface DeleteTransactionByIdOutputPort : Port<UUID, Unit>

