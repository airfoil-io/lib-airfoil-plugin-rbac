package io.airfoil.plugins.rbac.data.exposed.repository

import io.airfoil.plugins.rbac.data.domain.RoleGrantRepository
import io.airfoil.plugins.rbac.data.domain.dto.PermissionId
import io.airfoil.plugins.rbac.data.domain.dto.RoleGrant
import io.airfoil.plugins.rbac.data.domain.dto.RoleId
import io.airfoil.plugins.rbac.data.exposed.table.RoleGrantTable
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedRoleGrantRepository : RoleGrantRepository {
    override fun create(roleGrant: RoleGrant): RoleGrant = transaction {
        RoleGrantTable.new(
            roleId = roleGrant.roleId.value,
            permissionId = roleGrant.permissionId.value,
        ).toDTO()
    }

    override fun delete(roleGrant: RoleGrant): RoleGrant = transaction {
        RoleGrantTable.delete(
            roleId = roleGrant.roleId.value,
            permissionId = roleGrant.permissionId.value,
        ).toDTO()
    }

    override fun fetchByRole(id: RoleId): List<RoleGrant> = transaction {
        RoleGrantTable.findByRoleId(id.value).map { it.toDTO() }
    }

    override fun fetchByPermission(id: PermissionId): List<RoleGrant> = transaction {
        RoleGrantTable.findByPermissionId(id.value).map { it.toDTO() }
    }
}
