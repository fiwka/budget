package xyz.fiwka.budget.dataservice.application.port.out.budget

import xyz.fiwka.budget.dataservice.domain.budget.Budget
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface FindBudgetByIdOutputPort : Port<UUID, Budget?>

