package io.airfoil.plugins.rbac.data.exposed.repository

import io.airfoil.plugins.rbac.data.domain.ActorGrantRepository
import io.airfoil.plugins.rbac.data.domain.dto.ActorGrant
import io.airfoil.plugins.rbac.data.domain.dto.ActorId
import io.airfoil.plugins.rbac.data.exposed.table.ActorGrantTable
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedActorGrantRepository : ActorGrantRepository {
    override fun create(actorGrant: ActorGrant): ActorGrant = transaction {
        ActorGrantTable.new(
            actorId = actorGrant.actorId.value,
            permissionId = actorGrant.permissionId.value,
            subject = actorGrant.subject,
            subjectId = actorGrant.subjectId.value,
        ).toDTO()
    }

    override fun delete(actorGrant: ActorGrant): ActorGrant = transaction {
        ActorGrantTable.delete(
            actorId = actorGrant.actorId.value,
            permissionId = actorGrant.permissionId.value,
            subject = actorGrant.subject,
            subjectId = actorGrant.subjectId.value,
        ).toDTO()
    }

    override fun fetchByActor(id: ActorId): List<ActorGrant> = transaction {
        ActorGrantTable.findByActorId(id.value).map { it.toDTO() }
    }
}
