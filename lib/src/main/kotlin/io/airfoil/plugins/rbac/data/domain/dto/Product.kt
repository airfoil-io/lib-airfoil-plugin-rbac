package io.airfoil.plugins.rbac.data.domain.dto

import io.airfoil.common.extension.isUpperCase
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Product(val value: String) {
    
    init {
        require(value.isNotEmpty()) { "Product cannot be empty" }
        require(value.isUpperCase()) { "Product must be uppercase" }
    }

    override fun toString() = value.toString()

    companion object {
        operator fun invoke(string: String): Product? = runCatching {
            Product(string)
        }.getOrNull()
    }
}
