package xyz.fiwka.budget.port

fun interface Port<IN, OUT> {
    fun execute(request: IN): OUT
}