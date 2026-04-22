package xyz.fiwka.budget.dataservice.infrastructure.controller.budget

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
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.DeleteBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.DeleteBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListAccessibleBudgetsCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ListAccessibleBudgetsUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetUseCase
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.budget.AccessibleBudgetsQueryRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.budget.BudgetFieldsRequest
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.budget.AccessibleBudgetResponse
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.page.PageResponse
import xyz.fiwka.budget.dataservice.infrastructure.mapper.BudgetMapper
import java.util.UUID

@RestController
@Validated
@RequestMapping("/api/budget")
class BudgetController(
    private val budgetMapper: BudgetMapper,
    private val createBudgetUseCase: CreateBudgetUseCase,
    private val readBudgetUseCase: ReadBudgetUseCase,
    private val updateBudgetUseCase: UpdateBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
    private val listAccessibleBudgetsUseCase: ListAccessibleBudgetsUseCase,
) {

    @PostMapping
    fun createBudget(
        @Valid @RequestBody budgetFieldsRequest: BudgetFieldsRequest,
        authentication: Authentication,
    ) =
        budgetMapper.toDto(
            createBudgetUseCase.execute(
                CreateBudgetCommand(
                    name = budgetFieldsRequest.name,
                    description = budgetFieldsRequest.description,
                    actorLogin = authentication.name,
                )
            ).budget
        )

    @GetMapping("/{id}")
    fun readBudget(@PathVariable id: UUID, authentication: Authentication) =
        budgetMapper.toDto(readBudgetUseCase.execute(ReadBudgetCommand(id, authentication.name)).budget)

    @GetMapping("/accessible")
    fun listAccessibleBudgets(
        @Valid @ModelAttribute query: AccessibleBudgetsQueryRequest,
        authentication: Authentication,
    ): PageResponse<AccessibleBudgetResponse> {
        val response = listAccessibleBudgetsUseCase.execute(
            ListAccessibleBudgetsCommand(
                actorLogin = authentication.name,
                page = query.page,
                size = query.size,
                budgetId = query.budgetId,
                name = query.name,
                description = query.description,
                role = query.role,
            )
        ).budgets

        return PageResponse(
            items = response.items.map {
                AccessibleBudgetResponse(
                    id = requireNotNull(it.budget.id),
                    name = it.budget.name,
                    description = it.budget.description,
                    role = it.role,
                )
            },
            page = response.page,
            size = response.size,
            totalElements = response.totalElements,
            totalPages = response.totalPages,
        )
    }

    @PutMapping("/{id}")
    fun updateBudget(
        @PathVariable id: UUID,
        @Valid @RequestBody budgetFieldsRequest: BudgetFieldsRequest,
        authentication: Authentication,
    ) =
        budgetMapper.toDto(
            updateBudgetUseCase.execute(
                UpdateBudgetCommand(id, budgetFieldsRequest.name, budgetFieldsRequest.description, authentication.name)
            ).budget
        )

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBudget(@PathVariable id: UUID, authentication: Authentication) {
        deleteBudgetUseCase.execute(DeleteBudgetCommand(id, authentication.name))
    }
}