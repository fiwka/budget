package xyz.fiwka.budget.dataservice.application.port.out.auth

import xyz.fiwka.budget.dataservice.domain.user.User
import xyz.fiwka.budget.port.Port

interface GenerateJwtTokenOutputPort : Port<User, String>

