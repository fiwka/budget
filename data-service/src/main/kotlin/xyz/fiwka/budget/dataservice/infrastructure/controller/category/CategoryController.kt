package xyz.fiwka.budget.dataservice.infrastructure.controller.category

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import xyz.fiwka.budget.dataservice.application.port.`in`.category.CreateCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.CreateCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.category.DeleteCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.DeleteCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ListBudgetCategoriesCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ListBudgetCategoriesUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.ReadCategoryUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.category.UpdateCategoryCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.category.UpdateCategoryUseCase
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.category.CategoryFieldsRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.category.CategoryListQueryRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.category.CategoryResponse
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.page.PageResponse
import xyz.fiwka.budget.dataservice.infrastructure.mapper.CategoryMapper
import java.util.UUID

@RestController
@Validated
@RequestMapping("/api/category")
class CategoryController(
    private val categoryMapper: CategoryMapper,
    private val createCategoryUseCase: CreateCategoryUseCase,
    private val readCategoryUseCase: ReadCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val listBudgetCategoriesUseCase: ListBudgetCategoriesUseCase,
) {

    @PostMapping
    fun createCategory(
        @Valid @RequestBody categoryFieldsRequest: CategoryFieldsRequest,
        authentication: Authentication,
    ) =
        categoryMapper.toDto(
            createCategoryUseCase.execute(
                CreateCategoryCommand(
                    budgetId = categoryFieldsRequest.budgetId,
                    name = categoryFieldsRequest.name,
                    isConsumption = categoryFieldsRequest.isConsumption,
                    actorLogin = authentication.name,
                )
            ).category
        )

    @GetMapping("/{id}")
    fun readCategory(@PathVariable id: UUID, authentication: Authentication) =
        categoryMapper.toDto(readCategoryUseCase.execute(ReadCategoryCommand(id, authentication.name)).category)

    @GetMapping("/budget/{budgetId}")
    fun listBudgetCategories(
        @PathVariable budgetId: UUID,
        @Valid @ModelAttribute query: CategoryListQueryRequest,
        authentication: Authentication,
    ): PageResponse<CategoryResponse> {
        val response = listBudgetCategoriesUseCase.execute(
            ListBudgetCategoriesCommand(
                budgetId = budgetId,
                actorLogin = authentication.name,
                page = query.page,
                size = query.size,
                id = query.id,
                name = query.name,
                isConsumption = query.isConsumption,
            )
        ).categories

        return PageResponse(
            items = response.items.map(categoryMapper::toDto),
            page = response.page,
            size = response.size,
            totalElements = response.totalElements,
            totalPages = response.totalPages,
        )
    }

    @PutMapping("/{id}")
    fun updateCategory(
        @PathVariable id: UUID,
        @Valid @RequestBody categoryFieldsRequest: CategoryFieldsRequest,
        authentication: Authentication,
    ) =
        categoryMapper.toDto(
            updateCategoryUseCase.execute(
                UpdateCategoryCommand(
                    id,
                    categoryFieldsRequest.budgetId,
                    categoryFieldsRequest.name,
                    categoryFieldsRequest.isConsumption,
                    authentication.name,
                )
            ).category
        )

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCategory(@PathVariable id: UUID, authentication: Authentication) {
        deleteCategoryUseCase.execute(DeleteCategoryCommand(id, authentication.name))
    }
}

