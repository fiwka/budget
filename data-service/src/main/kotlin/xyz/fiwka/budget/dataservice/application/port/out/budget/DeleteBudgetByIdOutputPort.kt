package xyz.fiwka.budget.dataservice.application.port.out.budget

import xyz.fiwka.budget.port.Port
import java.util.UUID

interface DeleteBudgetByIdOutputPort : Port<UUID, Unit>

