package io.airfoil.plugins.rbac.data.domain.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Permission(
    @Transient val id: PermissionId = PermissionId.random(),
    val product: Product,
    val subject: Subject,
    val operation: Operation,
    val resource: Resource? = null,
    val description: String,
)

fun Permission.toName() = PermissionName(
    buildString {
        append(product)
        append(".")
        append(subject)
        append(".")
        resource?.also {
            append(resource)
            append(".")
        }
        append(operation)
    }.lowercase()
)
