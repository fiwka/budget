package xyz.fiwka.budget.dataservice.application.port.out.auth

import xyz.fiwka.budget.dataservice.domain.user.User

interface ReadRefreshTokenOutputPort {
    fun extractUsernameFromRefreshToken(token: String): String?
    fun isRefreshTokenValid(token: String, user: User): Boolean
}

