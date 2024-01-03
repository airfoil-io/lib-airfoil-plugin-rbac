package io.airfoil.plugins.rbac.data.exposed.table

import io.airfoil.plugins.rbac.data.domain.dto.PermissionId
import io.airfoil.plugins.rbac.data.domain.dto.RoleGrant
import io.airfoil.plugins.rbac.data.domain.dto.RoleId
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import java.util.UUID

object RoleGrantTable : Table(name = "role_grant") {
    val roleId = reference("role_id", RoleTable)
    val permissionId = reference("permission_id", PermissionTable)

    fun new(roleId: UUID, permissionId: UUID): RoleGrantRecord =
        RoleGrantTable.insert {
            it[this.roleId] = roleId
            it[this.permissionId] = permissionId
        }.let {
            RoleGrantRecord(
                roleId = roleId,
                permissionId = permissionId,
            )
        }

    fun delete(roleId: UUID, permissionId: UUID): RoleGrantRecord =
        RoleGrantTable.deleteWhere {
            (RoleGrantTable.roleId eq roleId) and 
            (RoleGrantTable.permissionId eq permissionId)
        }.let {
            RoleGrantRecord(
                roleId = roleId,
                permissionId = permissionId,
            )
        }

    fun findByRoleId(roleId: UUID): List<RoleGrantRecord> =
        RoleGrantTable.select { RoleGrantTable.roleId eq roleId }
            .withDistinct()
            .map {
                RoleGrantRecord(
                    roleId = it[RoleGrantTable.roleId].value,
                    permissionId = it[RoleGrantTable.permissionId].value,
                )
            }

    fun findByPermissionId(permissionId: UUID): List<RoleGrantRecord> =
        RoleGrantTable.select { RoleGrantTable.permissionId eq permissionId }
            .withDistinct()
            .map {
                RoleGrantRecord(
                    roleId = it[RoleGrantTable.roleId].value,
                    permissionId = it[RoleGrantTable.permissionId].value,
                )
            }
}

class RoleGrantRecord(
    val roleId: UUID,
    val permissionId: UUID,
) {
    fun toDTO(): RoleGrant = RoleGrant(
        roleId = RoleId(roleId),
        permissionId = PermissionId(permissionId),
    )
}
