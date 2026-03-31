package xyz.fiwka.budget.dataservice.infrastructure.controller.budget

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.CreateBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.DeleteBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.DeleteBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.ReadBudgetUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.budget.UpdateBudgetUseCase
import xyz.fiwka.budget.dataservice.infrastructure.dto.request.budget.BudgetFieldsRequest
import xyz.fiwka.budget.dataservice.infrastructure.mapper.BudgetMapper
import java.util.UUID

@RestController
@RequestMapping("/api/budget")
class BudgetController(
    private val budgetMapper: BudgetMapper,
    private val createBudgetUseCase: CreateBudgetUseCase,
    private val readBudgetUseCase: ReadBudgetUseCase,
    private val updateBudgetUseCase: UpdateBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase
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

    @GetMapping("/{id}")
    fun readBudget(@PathVariable id: UUID) =
        budgetMapper.toDto(readBudgetUseCase.execute(ReadBudgetCommand(id)).budget)

    @PutMapping("/{id}")
    fun updateBudget(
        @PathVariable id: UUID,
        @Valid @RequestBody budgetFieldsRequest: BudgetFieldsRequest
    ) =
        budgetMapper.toDto(
            updateBudgetUseCase.execute(
                UpdateBudgetCommand(id, budgetFieldsRequest.name, budgetFieldsRequest.description)
            ).budget
        )

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBudget(@PathVariable id: UUID) {
        deleteBudgetUseCase.execute(DeleteBudgetCommand(id))
    }
}