package io.airfoil.plugins.rbac.data.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class RoleGrant(
    val roleId: RoleId,
    val permissionId: PermissionId,
)
