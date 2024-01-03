package io.airfoil.plugins.rbac.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AccessControl(
    val permissions: Array<String>,
    val require: Require = Require.ALL_PERMISSIONS,
) {
    
    enum class Require {
        ALL_PERMISSIONS,
        ONE_PERMISSION,
    }
}
