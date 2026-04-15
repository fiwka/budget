package xyz.fiwka.budget.dataservice.infrastructure.dto.request.auth

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank
    val login: String,
    @field:NotBlank
    val password: String
)

