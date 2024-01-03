package io.airfoil.plugins.rbac.data.domain.dto

import io.airfoil.common.extension.enumByNameIgnoreCase
import kotlinx.serialization.Serializable
import kotlin.runCatching

@JvmInline
@Serializable
value class PermissionName(val value: String) {
    init {
        require(value.isNotEmpty()) { "Permission name cannot be empty" }
        value.split(".").also {
            require(it.size == 3 || it.size == 4) { "Invalid permission name" }
            require(it[it.size - 1].enumByNameIgnoreCase<Operation>() != null) { "Invalid permission name: invalid operation" }
        }

    }

    override fun toString() = value.toString()

    fun product(): String =
        value.split(".")[0].uppercase()

    fun subject(): Subject =
        value.split(".")[1].uppercase()

    fun resource(): String? =
        value.split(".").let {
            when (it.size) {
                4 -> it[2]
                else -> null
            }
        }

    fun operation(): Operation =
        value.split(".").let {
            it[it.size - 1].enumByNameIgnoreCase<Operation>()!!
        }

    companion object {
        operator fun invoke(string: String): PermissionName? = runCatching {
            PermissionName(string)
        }.getOrNull()
    }
}
