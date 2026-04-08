package xyz.fiwka.budget.dataservice.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import xyz.fiwka.budget.dataservice.infrastructure.entity.CategoryEntity
import java.util.UUID

interface CategoryRepository : JpaRepository<CategoryEntity, UUID>

