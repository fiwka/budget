package xyz.fiwka.budget.dataservice.application.exception

import java.lang.RuntimeException

abstract class DataServiceException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : RuntimeException()