package xyz.fiwka.budget.dataservice.infrastructure.controller.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import xyz.fiwka.budget.dataservice.application.exception.type.BadRequestException
import xyz.fiwka.budget.dataservice.application.exception.type.NotFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(exception: RuntimeException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.message)

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(exception: RuntimeException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.message)

    @ExceptionHandler(
        MethodArgumentNotValidException::class
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValid(exception: Exception): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed for the request body")
            .apply {
                setProperty(
                    "validationErrors",
                    (exception as MethodArgumentNotValidException).bindingResult.fieldErrors.map { fieldError ->
                        mapOf(
                            "field" to fieldError.field,
                            "rejectedValue" to fieldError.rejectedValue,
                            "message" to fieldError.defaultMessage
                        )
                    }
                )
            }

    @ExceptionHandler(
        ConstraintViolationException::class
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolation(exception: Exception): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed for the request parameters")
            .apply {
                setProperty(
                    "validationErrors",
                    (exception as ConstraintViolationException).constraintViolations.map { violation ->
                        mapOf(
                            "propertyPath" to violation.propertyPath.toString(),
                            "invalidValue" to violation.invalidValue,
                            "message" to violation.message
                        )
                    }
                )
            }

    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalServerError(exception: RuntimeException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.message)
            .apply {
                setProperty("exception", exception.javaClass.name)
            }
}