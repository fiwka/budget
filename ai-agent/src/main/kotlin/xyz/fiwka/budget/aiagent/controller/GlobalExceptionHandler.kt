package xyz.fiwka.budget.aiagent.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.function.client.WebClientResponseException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(ex: IllegalArgumentException): Map<String, String> = mapOf("message" to (ex.message ?: "Bad request"))

    @ExceptionHandler(IllegalAccessException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbidden(ex: IllegalAccessException): Map<String, String> = mapOf("message" to (ex.message ?: "Forbidden"))

    @ExceptionHandler(WebClientResponseException.Unauthorized::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorized(ex: WebClientResponseException.Unauthorized): Map<String, String> =
        mapOf("message" to (ex.message ?: "Unauthorized"))

    @ExceptionHandler(WebClientResponseException.Forbidden::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleUpstreamForbidden(ex: WebClientResponseException.Forbidden): Map<String, String> =
        mapOf("message" to (ex.message ?: "Forbidden"))
}
