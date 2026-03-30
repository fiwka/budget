package xyz.fiwka.budget.dataservice.application.exception.budget

import xyz.fiwka.budget.dataservice.application.exception.type.NotFoundException
import java.util.UUID

class BudgetNotFoundException(id: UUID) : NotFoundException("Budget with $id not found")