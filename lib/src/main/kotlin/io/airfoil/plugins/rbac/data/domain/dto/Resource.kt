package io.airfoil.plugins.rbac.data.domain.dto

import io.airfoil.common.extension.isUpperCase
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Resource(val value: String) {
    
    init {
        require(value.isNotEmpty()) { "Resource cannot be empty" }
        require(value.isUpperCase()) { "Resource must be uppercase" }
    }

    override fun toString() = value.toString()

    companion object {
        operator fun invoke(string: String): Resource? = runCatching {
            Resource(string)
        }.getOrNull()
    }
}
