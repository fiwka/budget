package xyz.fiwka.budget.dataservice.application.exception.type

import xyz.fiwka.budget.dataservice.application.exception.DataServiceException

open class NotFoundException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : DataServiceException()