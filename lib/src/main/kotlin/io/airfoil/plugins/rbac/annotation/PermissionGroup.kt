package io.airfoil.plugins.rbac.annotation

import io.airfoil.plugins.rbac.data.domain.dto.Subject

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PermissionGroup(
    val product: String,
    val subject: Subject,
    val permissions: Array<Permission>,
) {
}
