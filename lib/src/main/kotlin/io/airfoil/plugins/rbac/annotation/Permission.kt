package io.airfoil.plugins.rbac.annotation

import io.airfoil.plugins.rbac.data.domain.dto.Operation

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Permission(
    val operation: Operation,
    val resource: String = "",
    val description: String,
) {
}
