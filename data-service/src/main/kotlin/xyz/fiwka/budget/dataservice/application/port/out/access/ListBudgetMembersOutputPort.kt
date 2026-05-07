package xyz.fiwka.budget.dataservice.application.port.out.access

import xyz.fiwka.budget.dataservice.domain.budget.BudgetMember
import xyz.fiwka.budget.port.Port
import java.util.UUID

interface ListBudgetMembersOutputPort : Port<UUID, List<BudgetMember>>
