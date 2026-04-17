package xyz.fiwka.budget.dataservice.infrastructure.controller.user

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.GetUserInfoCommand
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.GetUserInfoUseCase
import xyz.fiwka.budget.dataservice.infrastructure.dto.response.auth.UserInfoResponse

@RestController
@RequestMapping("/api/user")
class UserController(
    private val getUserInfoUseCase: GetUserInfoUseCase
) {

    @GetMapping("/info")
    fun info(authentication: Authentication): UserInfoResponse {
        val response = getUserInfoUseCase.execute(GetUserInfoCommand(authentication.name))
        return UserInfoResponse(response.id, response.username, response.email)
    }
}

