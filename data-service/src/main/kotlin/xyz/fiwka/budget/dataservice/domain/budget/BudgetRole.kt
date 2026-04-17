package xyz.fiwka.budget.dataservice.domain.budget

enum class BudgetPermission {
    VIEW,
    EDIT,
    MANAGE,
}

enum class BudgetRole(
    val key: Int,
    val canView: Boolean,
    val canEdit: Boolean,
    val canManage: Boolean,
) {
    READER(0, true, false, false),
    EDITOR(1, true, true, false),
    ADMIN(2, true, true, true),
    OWNER(3, true, true, true);

    fun hasPermission(permission: BudgetPermission): Boolean =
        when (permission) {
            BudgetPermission.VIEW -> canView
            BudgetPermission.EDIT -> canEdit
            BudgetPermission.MANAGE -> canManage
        }

    companion object {
        fun fromKey(key: Int): BudgetRole =
            entries.firstOrNull { it.key == key }
                ?: throw IllegalArgumentException("Unknown budget role key: $key")
    }
}

