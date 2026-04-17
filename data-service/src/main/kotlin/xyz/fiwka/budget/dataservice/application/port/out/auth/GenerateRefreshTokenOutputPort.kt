package xyz.fiwka.budget.dataservice.application.port.out.auth

import xyz.fiwka.budget.dataservice.domain.user.User

interface GenerateRefreshTokenOutputPort {
    fun generateRefreshToken(user: User): String
}

