package io.airfoil.plugins.rbac.data.domain.dto

import kotlinx.serialization.Serializable

@Serializable
enum class Operation {
    READ,
    WRITE,
}
