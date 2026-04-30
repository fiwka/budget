package xyz.fiwka.budget.dataservice.infrastructure.controller.exception

import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import xyz.fiwka.budget.dataservice.application.exception.type.BadRequestException
import xyz.fiwka.budget.dataservice.application.exception.type.ConflictException
import xyz.fiwka.budget.dataservice.application.exception.type.ForbiddenException
import xyz.fiwka.budget.dataservice.application.exception.type.NotFoundException
import xyz.fiwka.budget.dataservice.application.exception.type.UnauthorizedException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(exception: RuntimeException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.message)

    @ExceptionHandler(
        BadRequestException::class,
        HttpMessageNotReadableException::class
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(exception: RuntimeException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.message)

    @ExceptionHandler(ConflictException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleConflict(exception: RuntimeException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.message)

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorized(exception: RuntimeException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.message)

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbidden(exception: RuntimeException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.message)

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
    fun handleInternalServerError(exception: RuntimeException): ProblemDetail {
        log.error("Internal Server Error", exception)
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.message)
            .apply {
                setProperty("exception", exception.javaClass.name)
            }
    }

    companion object {
        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }
}