package xyz.fiwka.budget.dataservice.infrastructure.dto.request.auth

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @field:NotBlank
    val refreshToken: String
)

