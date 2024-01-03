package io.airfoil.plugins.rbac.data.exposed.table

import io.airfoil.plugins.rbac.data.domain.dto.ActorId
import io.airfoil.plugins.rbac.data.domain.dto.ActorRole
import io.airfoil.plugins.rbac.data.domain.dto.RoleId
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import java.util.UUID

object ActorRoleTable : Table(name = "actor_role") {
    val actorId = uuid("actor_id")
    val roleId = reference("role_id", RoleTable)

    fun new(actorId: UUID, roleId: UUID): ActorRoleRecord =
        ActorRoleTable.insert {
            it[this.actorId] = actorId
            it[this.roleId] = roleId
        }.let {
            ActorRoleRecord(
                actorId = actorId,
                roleId = roleId,
            )
        }

    fun delete(actorId: UUID, roleId: UUID): ActorRoleRecord =
        ActorRoleTable.deleteWhere {
            (ActorRoleTable.actorId eq actorId) and 
            (ActorRoleTable.roleId eq roleId)
        }.let {
            ActorRoleRecord(
                actorId = actorId,
                roleId = roleId,
            )
        }

    fun findByActorId(actorId: UUID): List<ActorRoleRecord> =
        ActorRoleTable.select { ActorRoleTable.actorId eq actorId }
            .withDistinct()
            .map {
                ActorRoleRecord(
                    actorId = it[ActorRoleTable.actorId],
                    roleId = it[ActorRoleTable.roleId].value,
                )
            }

    fun findByRoleId(roleId: UUID): List<ActorRoleRecord> =
        ActorRoleTable.select { ActorRoleTable.roleId eq roleId }
            .withDistinct()
            .map {
                ActorRoleRecord(
                    actorId = it[ActorRoleTable.actorId],
                    roleId = it[ActorRoleTable.roleId].value,
                )
            }
}

class ActorRoleRecord(
    val actorId: UUID,
    val roleId: UUID,
) {
    fun toDTO(): ActorRole = ActorRole(
        actorId = ActorId(actorId),
        roleId = RoleId(roleId),
    )
}
