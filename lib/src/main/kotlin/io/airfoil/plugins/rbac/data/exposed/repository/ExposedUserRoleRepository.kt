package io.airfoil.plugins.rbac.data.exposed.repository

import io.airfoil.plugins.rbac.data.domain.ActorRoleRepository
import io.airfoil.plugins.rbac.data.domain.dto.ActorId
import io.airfoil.plugins.rbac.data.domain.dto.ActorRole
import io.airfoil.plugins.rbac.data.domain.dto.RoleId
import io.airfoil.plugins.rbac.data.exposed.table.ActorRoleTable
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedActorRoleRepository : ActorRoleRepository {
    override fun create(actorRole: ActorRole): ActorRole = transaction {
        ActorRoleTable.new(
            actorId = actorRole.actorId.value,
            roleId = actorRole.roleId.value,
        ).toDTO()
    }

    override fun delete(actorRole: ActorRole): ActorRole = transaction {
        ActorRoleTable.delete(
            actorId = actorRole.actorId.value,
            roleId = actorRole.roleId.value,
        ).toDTO()
    }

    override fun fetchByRole(id: RoleId): List<ActorRole> = transaction {
        ActorRoleTable.findByRoleId(id.value).map { it.toDTO() }
    }

    override fun fetchByActor(id: ActorId): List<ActorRole> = transaction {
        ActorRoleTable.findByActorId(id.value).map { it.toDTO() }
    }
}
