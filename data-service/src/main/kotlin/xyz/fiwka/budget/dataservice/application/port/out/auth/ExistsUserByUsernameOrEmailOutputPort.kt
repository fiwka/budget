package xyz.fiwka.budget.dataservice.application.port.out.auth

import xyz.fiwka.budget.port.Port

interface ExistsUserByUsernameOrEmailOutputPort : Port<ExistsUserByUsernameOrEmailQuery, Boolean>

data class ExistsUserByUsernameOrEmailQuery(
    val username: String,
    val email: String
)

