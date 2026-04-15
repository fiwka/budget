package xyz.fiwka.budget.dataservice.infrastructure.configuration.usecase.auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.LoginUseCase
import xyz.fiwka.budget.dataservice.application.port.`in`.auth.RegisterUseCase
import xyz.fiwka.budget.dataservice.application.port.out.auth.ExistsUserByUsernameOrEmailOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.FindUserByLoginOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.GenerateJwtTokenOutputPort
import xyz.fiwka.budget.dataservice.application.port.out.auth.SaveUserOutputPort
import xyz.fiwka.budget.dataservice.application.service.auth.LoginService
import xyz.fiwka.budget.dataservice.application.service.auth.RegisterService

@Configuration
class AuthUseCaseConfiguration {

    @Bean
    fun registerUseCase(
        existsUserByUsernameOrEmailOutputPort: ExistsUserByUsernameOrEmailOutputPort,
        saveUserOutputPort: SaveUserOutputPort,
        passwordEncoder: PasswordEncoder
    ): RegisterUseCase =
        RegisterService(existsUserByUsernameOrEmailOutputPort, saveUserOutputPort, passwordEncoder)

    @Bean
    fun loginUseCase(
        findUserByLoginOutputPort: FindUserByLoginOutputPort,
        generateJwtTokenOutputPort: GenerateJwtTokenOutputPort,
        passwordEncoder: PasswordEncoder
    ): LoginUseCase =
        LoginService(findUserByLoginOutputPort, generateJwtTokenOutputPort, passwordEncoder)
}

