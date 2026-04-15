package xyz.fiwka.budget.dataservice.domain.budget

enum class BudgetRole(val key: Int) {
    READER(0),
    EDITOR(1),
    ADMIN(2),
    OWNER(3);

    companion object {
        fun fromKey(key: Int): BudgetRole =
            entries.firstOrNull { it.key == key }
                ?: throw IllegalArgumentException("Unknown budget role key: $key")
    }
}

