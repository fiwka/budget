package xyz.fiwka.budget.dataservice.infrastructure.mapper

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.infrastructure.entity.CategoryEntity
import java.util.UUID

class CategoryMapperTest {

    private val mapper = CategoryMapper()

    @Test
    fun `toEntity keeps isConsumption true`() {
        val category = Category(
            id = null,
            budgetId = UUID.randomUUID(),
            name = "Food",
            isConsumption = true
        )

        val entity = mapper.toEntity(category)

        assertTrue(entity.isConsumption)
    }

    @Test
    fun `fromEntity keeps isConsumption false`() {
        val entity = CategoryEntity().apply {
            budgetId = UUID.randomUUID()
            name = "Salary"
            isConsumption = false
        }

        val category = mapper.fromEntity(entity)

        assertFalse(category.isConsumption)
    }
}

