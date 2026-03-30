package xyz.fiwka.budget.dataservice.infrastructure.dto.request.budget

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class BudgetFieldsRequest(
    @NotBlank
    @Size(max = 255, min = 4)
    val name: String,
    @NotBlank
    @Size(max = 1500)
    val description: String
)