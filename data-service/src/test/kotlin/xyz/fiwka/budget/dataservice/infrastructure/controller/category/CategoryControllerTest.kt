package xyz.fiwka.budget.dataservice.infrastructure.controller.category

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.TestingAuthenticationToken
import xyz.fiwka.budget.dataservice.application.model.page.PageResult
import xyz.fiwka.budget.dataservice.application.port.`in`.category.CreateCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.CreateCategoryResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.category.CreateCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.category.DeleteCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.DeleteCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ListBudgetCategoriesCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ListBudgetCategoriesResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ListBudgetCategoriesUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.category.UpdateCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.UpdateCategoryResponse
import xyz.fiwka.budget.dataservice.application.port.`in`.category.UpdateCategoryUseCase
import xyz.fiwka.budget.dataservice.domain.category.Category
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.category.CategoryFieldsRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.category.CategoryListQueryRequest
import xyz.fiwka.budget.dataservice.infrastructure.mapper.CategoryMapper
import java.util.UUID

class CategoryControllerTest {

    @Test
    fun `should create read update delete and list categories`() {
        val budgetId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        var created: CreateCategoryCommand? = null
        var read: ReadCategoryCommand? = null
        var updated: UpdateCategoryCommand? = null
        var deleted: DeleteCategoryCommand? = null
        var listed: ListBudgetCategoriesCommand? = null
        val controller = CategoryController(
            categoryMapper = CategoryMapper(),
            createCategoryUseCase = object : CreateCategoryUseCase {
                override fun execute(request: CreateCategoryCommand): CreateCategoryResponse {
                    created = request
                    return CreateCategoryResponse(Category(categoryId, request.budgetId, request.name, request.isConsumption))
                }
            },
            readCategoryUseCase = object : ReadCategoryUseCase {
                override fun execute(request: ReadCategoryCommand): ReadCategoryResponse {
                    read = request
                    return ReadCategoryResponse(Category(request.id, budgetId, "Food", true))
                }
            },
            updateCategoryUseCase = object : UpdateCategoryUseCase {
                override fun execute(request: UpdateCategoryCommand): UpdateCategoryResponse {
                    updated = request
                    return UpdateCategoryResponse(Category(request.id, request.budgetId, request.name, request.isConsumption))
                }
            },
            deleteCategoryUseCase = object : DeleteCategoryUseCase {
                override fun execute(request: DeleteCategoryCommand) {
                    deleted = request
                }
            },
            listBudgetCategoriesUseCase = object : ListBudgetCategoriesUseCase {
                override fun execute(request: ListBudgetCategoriesCommand): ListBudgetCategoriesResponse {
                    listed = request
                    return ListBudgetCategoriesResponse(
                        PageResult(
                            items = listOf(Category(categoryId, request.budgetId, "Food", true)),
                            page = request.page,
                            size = request.size,
                            totalElements = 1,
                            totalPages = 1,
                        )
                    )
                }
            },
        )
        val auth = TestingAuthenticationToken("alex", "credentials")

        assertEquals(categoryId, controller.createCategory(CategoryFieldsRequest(budgetId, "Food", true), auth).id)
        assertEquals("Food", controller.readCategory(categoryId, auth).name)
        assertEquals("Groceries", controller.updateCategory(categoryId, CategoryFieldsRequest(budgetId, "Groceries", false), auth).name)
        controller.deleteCategory(categoryId, auth)
        val page = controller.listBudgetCategories(
            budgetId,
            CategoryListQueryRequest(name = "Fo", isConsumption = true, page = 1, size = 5),
            auth,
        )

        assertEquals(CreateCategoryCommand(budgetId, "Food", true, "alex"), created)
        assertEquals(ReadCategoryCommand(categoryId, "alex"), read)
        assertEquals(UpdateCategoryCommand(categoryId, budgetId, "Groceries", false, "alex"), updated)
        assertEquals(DeleteCategoryCommand(categoryId, "alex"), deleted)
        assertEquals("Fo", listed?.name)
        assertEquals(1, page.items.size)
    }
}
