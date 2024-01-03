package io.airfoil.plugins.rbac.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Permissions(
    val groups: Array<PermissionGroup>,
) {
}
