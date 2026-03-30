package xyz.fiwka.budget.dataservice.infrastructure.controller.budget

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetUseCase
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.budget.BudgetFieldsRequest
import xyz.fiwka.budget.dataservice.infrastructure.mapper.BudgetMapper

@RestController
@RequestMapping("/api/budget")
class BudgetController(
    private val budgetMapper: BudgetMapper,
    private val createBudgetUseCase: CreateBudgetUseCase
) {

    @PostMapping
    fun createBudget(
        @Valid @RequestBody budgetFieldsRequest: BudgetFieldsRequest
    ) =
        budgetMapper.toDto(
            createBudgetUseCase.execute(
                budgetMapper.toCommand(
                    budgetFieldsRequest
                )
            ).budget
        )
}