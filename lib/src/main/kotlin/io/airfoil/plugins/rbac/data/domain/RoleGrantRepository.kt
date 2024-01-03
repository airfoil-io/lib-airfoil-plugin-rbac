package io.airfoil.plugins.rbac.data.domain

import io.airfoil.plugins.rbac.data.domain.dto.PermissionId
import io.airfoil.plugins.rbac.data.domain.dto.RoleGrant
import io.airfoil.plugins.rbac.data.domain.dto.RoleId

interface RoleGrantRepository {
    // create a new role grant
    fun create(roleGrant: RoleGrant): RoleGrant

    // delete a role grant
    fun delete(roleGrant: RoleGrant): RoleGrant

    // delete all role grants by role
    fun deleteAllByRole(id: RoleId) =
        fetchByRole(id).forEach { delete(it) }

    // fetch role grants by role
    fun fetchByRole(id: RoleId): List<RoleGrant>

    // fetch role grants by permission
    fun fetchByPermission(id: PermissionId): List<RoleGrant>
}
