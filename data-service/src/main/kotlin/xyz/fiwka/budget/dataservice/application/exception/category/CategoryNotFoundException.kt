package xyz.fiwka.budget.dataservice.application.exception.category

import xyz.fiwka.budget.dataservice.application.exception.type.NotFoundException
import java.util.UUID

class CategoryNotFoundException(id: UUID) : NotFoundException("Category with $id not found")